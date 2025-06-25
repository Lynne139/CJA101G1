package com.resto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.PeriodVO;

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
	
    // 新增入資歷庫
    @Transactional
    public void insert(PeriodVO periodVO) {
    	// sortOrder設為最末碼+1
    	int max = periodRepository
                .findMaxSortOrderByRestoId(periodVO.getRestoVO().getRestoId());
    	periodVO.setSortOrder(max + 1);
    	
    	periodRepository.save(periodVO);
    }
    
    // 更新入資歷庫
    @Transactional
    public void update(PeriodVO periodVO) {
        periodRepository.save(periodVO);
    }
    
    // 檢查重名
    public boolean existsDuplicateName(PeriodVO periodVO) {
    	Integer restoId    = periodVO.getRestoVO().getRestoId();
        String  periodName = periodVO.getPeriodName();
    	
        List<PeriodVO> matches = periodRepository.findAllByRestoVO_RestoIdAndPeriodName(restoId, periodName);
   
        if (periodVO.getPeriodId() == null) {
        	// 新增
            return !matches.isEmpty();
        } else {
        	// 編輯
            return matches.stream().anyMatch(r -> !r.getPeriodId().equals(periodVO.getPeriodId()));
        }
    }
    
    // 刪除
    @Transactional
    public void deleteById(Integer periodId) {
        periodRepository.findById(periodId).ifPresent(periodVO -> {
            periodRepository.delete(periodVO); // 用 delete(entity) 而不是 deleteById
        });
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
    
    
}
