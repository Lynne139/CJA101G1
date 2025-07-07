package com.resto.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.resto.dto.RestoDTO;
import com.resto.entity.RestoVO;
import com.resto.utils.RestoCriteriaHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;

@Service("restoService")
public class RestoService {

	@PersistenceContext
    private EntityManager em;

	@Autowired
    RestoRepository restoRepository;
	@Autowired
	private ReservationService reservationService;

    
    // 複合查詢（Criteria 結構）
//    @Transactional(readOnly = true)
//    public List<RestoDTO> compositeQuery(Map<String, String[]> paramMap) {
//		List<RestoVO> voList = RestoCriteriaHelper.getAll(paramMap, em);
//	  return voList.stream()
//	      .map(vo -> new RestoDTO(
//	          vo.getRestoId(),
//	          vo.getRestoName(),
//	          vo.getRestoNameEn(),
//	          vo.getRestoLoc(),
//	          vo.getRestoSeatsTotal(),
//	          vo.getIsEnabled()
//	      ))
//	      .collect(Collectors.toList());
//        return RestoCriteriaHelper.getAll(paramMap, em);
//    }
   
    @Transactional(readOnly = true)
    public List<RestoDTO> compositeQueryAsDTO(Map<String, String[]> paramMap) {
        return RestoCriteriaHelper.getAllDTO(paramMap, em);
    }
    
    // 多筆(前台上架)
    @Transactional(readOnly = true)
    public List<RestoVO> getAllEnabled() {
        return restoRepository.findByIsDeletedFalseAndIsEnabledTrue();
    }

    // 多筆(後台無軟刪)
    @Transactional(readOnly = true)
    public List<RestoVO> getAll() {
    	return restoRepository.findByIsDeletedFalse();
    }
    
    // id拿單筆(允許後台抓得到已軟刪資料)
    @Transactional(readOnly = true)
    public RestoVO getById(Integer id) {
        return restoRepository.findById(id).orElse(null);
    }

    // id拿單筆(前台限上架且未軟刪)
    // 查單筆給前台(必須上架且非軟刪)
    public Optional<RestoVO> findOnline(Integer id) {            // 前台用
        return restoRepository.findByRestoIdAndIsDeletedFalseAndIsEnabledTrue(id);
    }
    
    

    // 軟刪除
    @Transactional
    public void softDelete(Integer id) {
        restoRepository.findById(id).ifPresent(vo -> {
            vo.setIsDeleted(true);
            restoRepository.save(vo);
        });
    }
    
    // 檢查重名
    public boolean existsDuplicateName(RestoVO resto) {
        List<RestoVO> matches = restoRepository.findAllByRestoNameAndIsDeletedFalse(resto.getRestoName());

        if (resto.getRestoId() == null) {
            return !matches.isEmpty();
        } else {
            return matches.stream().anyMatch(r -> !r.getRestoId().equals(resto.getRestoId()));
        }
    }

    // 重組欄位與圖片存入資料庫
    @Transactional
    public void saveWithImage(RestoVO vo, MultipartFile img, String clearImgFlag) {
        RestoVO target;
        
        // 記住舊值
        Integer oldSeatsTotal = null;

        if (vo.getRestoId() == null) {
            // 新增
            target = vo;            
        } else {
            // 編輯：抓出原始資料（含 version）
            target = restoRepository.findById(vo.getRestoId())
                      .orElseThrow(() -> new IllegalArgumentException("查無資料"));

            //檢查版本一致性
            if (!target.getVersion().equals(vo.getVersion())) {
                throw new OptimisticLockException("資料已被他人修改，請重新載入");
            }
            
            oldSeatsTotal = target.getRestoSeatsTotal();
            
                        
            // 更新欄位
            target.setRestoName(vo.getRestoName());
            target.setRestoNameEn(vo.getRestoNameEn());
            target.setRestoSeatsTotal(vo.getRestoSeatsTotal());
            target.setRestoInfo(vo.getRestoInfo());
            target.setRestoType(vo.getRestoType());
            target.setRestoContent(vo.getRestoContent());
            target.setRestoPhone(vo.getRestoPhone());
            target.setRestoLoc(vo.getRestoLoc());
            target.setIsEnabled(vo.getIsEnabled());
        }

        // 處理圖片
        if ("true".equalsIgnoreCase(clearImgFlag)) {
            target.setRestoImg(null);
        } else if (img != null && !img.isEmpty()) {
            try {
                target.setRestoImg(img.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("圖片處理錯誤", e);
            }
        }

        // 儲存
        restoRepository.save(target);
        
        // 若座位量有改，同步 reservation 快照
        if (oldSeatsTotal != null
            && !oldSeatsTotal.equals(target.getRestoSeatsTotal())) {

            reservationService.refreshSeatsTotal(target.getRestoId(),
                                              target.getRestoSeatsTotal());
        }
        
    }

    
    
    
    
    
}
