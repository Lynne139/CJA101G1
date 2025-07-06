package com.resto.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.resto.controller.SseController;
import com.resto.entity.RestoOrderVO;
import com.resto.model.RestoOrderRepository;


@Component
@Transactional
public class RestoScheduler {
	
	@Autowired
	RestoOrderRepository orderRepo;
	@Autowired
	private SseController sseController;
	
	
	
	// 間隔 60 秒執行一次
//    @Scheduled(fixedRate = 60_000)
//	 每日 04:00~23:59間，每1min都觸發一次
	@Scheduled(cron = "0 */1 4-23 * * ?") // 整點 0 秒 每n分 每小時 每日 每月 星期(?不指定，與日期擇一） 暫關
    // 定時更新訂單狀態(保留、逾期)
	public void updateRestoOrderStatus() {
        LocalDateTime now = LocalDateTime.now();
        // CREATED → WITHHOLD
        List<RestoOrderVO> createdList = orderRepo.findTodayOrdersByOrderStatus(LocalDate.now(), RestoOrderStatus.CREATED);
        for (RestoOrderVO o : createdList) {
            if (o.getReserveStartTime() != null && !now.isBefore(o.getReserveStartTime())) {
                o.setOrderStatus(RestoOrderStatus.WITHHOLD);
            }
        }
        // WITHHOLD → NOSHOW
        List<RestoOrderVO> withholdList = orderRepo.findTodayOrdersByOrderStatus(LocalDate.now(),RestoOrderStatus.WITHHOLD);
        for (RestoOrderVO o : withholdList) {
            if (o.getReserveExpireTime() != null && !now.isBefore(o.getReserveExpireTime())) {
                o.setOrderStatus(RestoOrderStatus.NOSHOW);
            }
        }
        
        // 有狀態改變才呼叫SSE
        if (!createdList.isEmpty() || !withholdList.isEmpty()) {
            sseController.sendStatusUpdate("refresh");
        }
        
        
        
    }
	
	
	
	
	
    
    
    
    
    
    
    
}
	
	


