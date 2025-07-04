package com.resto.model;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.PeriodVO;
import com.resto.integration.room.RestoPeriodCode;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PeriodService {
	
	@Autowired
    private PeriodRepository periodRepository;

	// 餐廳拿多筆
	@Transactional(readOnly = true)
    public List<PeriodVO> getPeriodsByRestoId(Integer restoId) {
		return periodRepository.findByRestoVO_RestoIdOrderBySort(restoId);
		}
	
	// id拿單筆
    @Transactional(readOnly = true)
    public PeriodVO getById(Integer periodId) {
        return periodRepository.findById(periodId).orElse(null);
    }
	
    // 新增入資料庫
    @Transactional
    public void insert(PeriodVO periodVO) {
    	// sortOrder設為最末碼+1
    	int max = periodRepository
                .findMaxSortOrderByRestoId(periodVO.getRestoVO().getRestoId());
    	periodVO.setSortOrder(max + 1);
    	
    	periodRepository.save(periodVO);
    }
    
    // 更新入資料庫
    @Transactional
    public void update(PeriodVO periodVO) {
    	
    	PeriodVO origin = periodRepository.findById(periodVO.getPeriodId())
                .orElseThrow(() ->
                   new EntityNotFoundException("找不到 Period"));
    	
    	// 只改需要的欄位，其餘欄位與 timeslotList 維持原狀
        origin.setPeriodName(periodVO.getPeriodName());
    }
    
    // 檢查重名
    public boolean existsDuplicateName(PeriodVO periodVO) {
    	Integer restoId    = periodVO.getRestoVO().getRestoId();
        String  periodName = periodVO.getPeriodName();
    	
        List<PeriodVO> matches = periodRepository.findAllByRestoVO_RestoIdAndPeriodName(restoId, periodName);
        
        // 名稱沒變，少一次DBround-trip
        if (periodVO.getPeriodId() != null &&
        	    periodName.equals(periodRepository.getById(periodVO.getPeriodId()).getPeriodName())) {
        	    return false;
        	}
        
        if (periodVO.getPeriodId() == null) {
        	// 新增
            return !matches.isEmpty();
        } else {
        	// 編輯
            return matches.stream().anyMatch(p -> !p.getPeriodId().equals(periodVO.getPeriodId()));
        }
    }
    
    // 刪除
    @Transactional
    public void deleteById(Integer periodId) {
    	periodRepository.deleteById(periodId);
    }

    
    // 排序更動
    @Transactional
    public void move(Integer periodId, String dir){
        PeriodVO self = periodRepository.findById(periodId)
                     .orElseThrow(() -> new IllegalArgumentException("找不到資料"));
        
        Integer restoId   = self.getRestoVO().getRestoId();
        Integer selfOrder = self.getSortOrder();
        
        // 上/下個物件
        Optional<PeriodVO> optTarget =
        		"up".equals(dir)
                ? periodRepository.findTopByRestoVO_RestoIdAndSortOrderLessThanOrderBySortOrderDesc(restoId, self.getSortOrder())
                : periodRepository.findTopByRestoVO_RestoIdAndSortOrderGreaterThanOrderBySortOrderAsc(restoId, self.getSortOrder());

        // 若已經在頂/底，target為empty
        if (optTarget.isEmpty()) return;
        PeriodVO target = optTarget.get();
        Integer targetOrder = target.getSortOrder();

        
        // 交換 sortOrder
        // 避免uk報錯，暫存self到一個-1
        self.setSortOrder(-1);
        periodRepository.saveAndFlush(self); //保險先flush讓DB更新

        target.setSortOrder(selfOrder);
        periodRepository.saveAndFlush(target);

        
        self.setSortOrder(targetOrder);
        periodRepository.save(self);
        
    }
    
    
    
    
    // ===== 住宿訂單相關 =====
    @Transactional
    public void setCode(Integer periodId, RestoPeriodCode code, Integer restoId) {

        Objects.requireNonNull(code, "code 不可為空");

        PeriodVO period = periodRepository.findById(periodId)
                              .orElseThrow(() -> new NoSuchElementException("period"));

        if (!Objects.equals(period.getRestoVO().getRestoId(), restoId)) {
            throw new IllegalArgumentException("restoId 不符，拒絕操作");
        }

        // 取消同餐廳其他區段的相同 code
        periodRepository.findByRestoVO_RestoIdAndPeriodCode(restoId, code)
            .filter(p -> !p.getPeriodId().equals(periodId))
            .ifPresent(p -> p.setPeriodCode(null));
        periodRepository.flush();

        period.setPeriodCode(code);

        // 如果 DB 有唯一索引，可省略 flush
        periodRepository.flush();
    }

    @Transactional
    public void clearCode(Integer periodId, Integer restoId) {
        int rows = periodRepository.clearPeriodCode(periodId, restoId);
        if (rows == 0)
            throw new IllegalArgumentException("找不到對應 period");
    }
    
    
    
    
    
    
    
    
    
    
    
}
