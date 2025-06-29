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
	        return timeslotRepository.findById(id).orElseThrow();
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
	            restore.setPeriodVO(timeslotVO.getPeriodVO()); // 重綁現在的period
	            timeslotRepository.save(restore);
	            return; 
	        }

	        timeslotRepository.save(timeslotVO);
	    }

	    
	    // 更新入資料庫
	    @Transactional
	    public void update(TimeslotVO updated) {
	    	TimeslotVO original = timeslotRepository.findById(updated.getTimeslotId()).orElseThrow();

	        // 若名稱沒變，不更新名稱，但拖曳也共用方法所以要更新periodId
	        if (original.getTimeslotName().equals(updated.getTimeslotName())) {
	        	original.setPeriodVO(updated.getPeriodVO()); // 把拖曳的新 period 套進去
	            timeslotRepository.save(original);
	        	return;
	        }

	        // 若名稱改變，檢查是否已有軟刪版
	        Optional<TimeslotVO> softDeleted = timeslotRepository
	            .findByRestoVO_RestoIdAndTimeslotNameAndIsDeletedTrue(updated.getRestoVO().getRestoId(),
	            													  updated.getTimeslotName());
	        // 有同名軟刪便救回資料
	        if (softDeleted.isPresent()) {
	            TimeslotVO toRestore = softDeleted.get();
	            
	            toRestore.setIsDeleted(false);
	            toRestore.setPeriodVO(updated.getPeriodVO()); // 更新Period
	            timeslotRepository.save(toRestore);

	            // 將原本的資料軟刪
	            original.setIsDeleted(true);
	            original.setPeriodVO(null); // 解除原period關聯
	            timeslotRepository.save(original);

	        } else {
	            // 沒有同名軟刪，正常更新
	            original.setTimeslotName(updated.getTimeslotName());
	            original.setPeriodVO(updated.getPeriodVO());    // 同步 period
	            timeslotRepository.save(original);
	        }
	    }

	    // 軟刪除
	    @Transactional
	    public void softDelete(Integer timeslotId) {
	        	//設period為null
	        	TimeslotVO ts = timeslotRepository.findById(timeslotId).orElseThrow();
	            ts.setIsDeleted(true);
	            ts.setPeriodVO(null); // 解除跟period的關聯
	            timeslotRepository.save(ts);	        	
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
