package com.resto.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.resto.utils.RestoCriteriaHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;

@Service("restoService")
public class RestoService {

	@PersistenceContext
    private EntityManager em;

    private final RestoRepository restoRepository;

    public RestoService(RestoRepository restoRepository) {
        this.restoRepository = restoRepository;
    }

    public List<RestoVO> getAll() {
        return restoRepository.findByIsDeletedFalse();
    }
    
    public RestoVO getById(Integer id) {
        return restoRepository.findById(id).orElse(null);
    }

    public RestoVO insert(RestoVO vo) {
        return restoRepository.save(vo);
    }

    public RestoVO save(RestoVO vo) {
    	return restoRepository.save(vo);
    }

    public void softDelete(Integer id) {
        restoRepository.findById(id).ifPresent(vo -> {
            vo.setIsDeleted(true);
            restoRepository.save(vo);
        });
    }

    // 複合查詢（Criteria 結構）
    public List<RestoVO> compositeQuery(Map<String, String[]> map) {
        return RestoCriteriaHelper.getAll(map, em);
    }
    
    
    // 用於新增時（單一參數）
    public boolean isDuplicateName(String name) {
        return restoRepository.findByRestoNameAndIsDeletedFalse(name).isPresent();
    }

    // 用於編輯時（排除自己）
    public boolean isDuplicateName(String name, Integer excludeId) {
        Optional<RestoVO> existing = restoRepository.findByRestoNameAndIsDeletedFalse(name);
        return existing.isPresent() && !existing.get().getRestoId().equals(excludeId);
    }

    
    @Transactional
    public void saveWithImage(RestoVO vo, MultipartFile img, String clearImgFlag) {
        RestoVO target;

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
    }

    
    
    
    
    
}
