package com.resto.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.PeriodVO;
import com.resto.entity.TimeslotVO;


	@Service
	public class TimeslotService {

		@Autowired
	    private TimeslotRepository timeslotRepository;

	    // 餐廳+區段拿多筆(未刪)
	    @Transactional(readOnly = true)
	    public List<TimeslotVO> getTimeslotsByRestoId(Integer restoId) {
	        return timeslotRepository.findActiveWithPeriodByResto(restoId);
	    }

	    
	    // id拿單筆
	    @Transactional(readOnly = true)
	    public TimeslotVO getById(Integer id) {
	        return timeslotRepository.findById(id).orElse(null);
	    }

	    
	    // 新增入資料庫
	    @Transactional
	    public void insert(TimeslotVO timeslotVO) {

	        Integer restoId = timeslotVO.getRestoVO().getRestoId();
	        String timeslotName = timeslotVO.getTimeslotName();

	        // 找軟刪中 同餐廳+同名 的timeslot
	        Optional<TimeslotVO> softDeleted =
	            timeslotRepository.findByRestoVO_RestoIdAndTimeslotNameAndIsDeletedTrue(restoId, timeslotName);

	        // 若軟刪中有則復原
	        if (softDeleted.isPresent()) {
	            TimeslotVO restore = softDeleted.get();
	            restore.setIsDeleted(false);
	            // 放到新選的period
	            restore.setPeriodVO(timeslotVO.getPeriodVO());
	            timeslotRepository.save(restore);
	            return; 
	        }

	        timeslotRepository.save(timeslotVO);
	    }

	    
	    // 更新入資料庫
	    @Transactional
	    public void update(TimeslotVO timeslotVO) {
	        timeslotRepository.save(timeslotVO);
	    }

	    // 軟刪除
	    @Transactional
	    public void softDelete(Integer timeslotId) {
	        timeslotRepository.findById(timeslotId).ifPresent(ts -> {
	            ts.setIsDeleted(true);
	            timeslotRepository.save(ts);
	        });
	    }

	    
	    // 檢查重名
	    public boolean existsDuplicateName(TimeslotVO timeslotVO) {

	        Integer restoId  = timeslotVO.getRestoVO().getRestoId();
	        String  timeslotName = timeslotVO.getTimeslotName();

	        List<TimeslotVO> matches =
	            timeslotRepository.findByRestoVO_RestoIdAndTimeslotNameAndIsDeletedFalse(restoId, timeslotName);

	        if (timeslotVO.getTimeslotId() == null) {
	            // 新增
	            return !matches.isEmpty();
	        } else {
	            // 編輯
	            return matches.stream()
	                          .anyMatch(t -> !t.getTimeslotId().equals(timeslotVO.getTimeslotId()));
	        }
	    }

    
    
    
    
    
    
    
    
    


}
