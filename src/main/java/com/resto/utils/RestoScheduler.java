package com.resto.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.resto.controller.SseController;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoReservationVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.model.ReservationRepository;
import com.resto.model.RestoOrderRepository;
import com.resto.model.RestoRepository;
import com.resto.model.TimeslotRepository;


@Component
@Transactional
public class RestoScheduler {
	
	@Autowired
	RestoOrderRepository orderRepo;
	@Autowired
	private SseController sseController;
	
	@Autowired
    private RestoRepository     restoRepo;
    @Autowired
    private TimeslotRepository  timeslotRepo;
    @Autowired
    private ReservationRepository reservationRepo;
	
	
	
    // ===== 定時更新訂單狀態(保留、逾期) =====
    
	// 間隔 60 秒執行一次
//    @Scheduled(fixedRate = 60_000)
//	 每日 04:00~23:59間，每1min都觸發一次
	@Scheduled(cron = "0 */1 00-23 * * ?") // 整點 0 秒 每n分 每小時 每日 每月 星期(?不指定，與日期擇一） 暫關
	public void updateRestoOrderStatus() {
		
		LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        
        // 用來收集實際改變狀態的訂單
        List<RestoOrderVO> changed = new ArrayList<>();
        
        // CREATED → WITHHOLD
        List<RestoOrderVO> createdList = orderRepo.findTodayOrdersByOrderStatus(LocalDate.now(), RestoOrderStatus.CREATED);
        for (RestoOrderVO o : createdList) {
            if (o.getReserveStartTime() != null && !now.isBefore(o.getReserveStartTime())) {
                o.setOrderStatus(RestoOrderStatus.WITHHOLD);
                changed.add(o);
            }
        }
        // WITHHOLD → NOSHOW
        List<RestoOrderVO> withholdList = orderRepo.findTodayOrdersByOrderStatus(LocalDate.now(),RestoOrderStatus.WITHHOLD);
        for (RestoOrderVO o : withholdList) {
            if (o.getReserveExpireTime() != null && !now.isBefore(o.getReserveExpireTime())) {
                o.setOrderStatus(RestoOrderStatus.NOSHOW);
                changed.add(o);
            }
        }
        
        // 有改動才批次存檔 & SSE推播
        if (!changed.isEmpty()) {
            // 先flush進資料庫，確保狀態已經commit
            orderRepo.saveAll(changed); 

            // refresh一次推
            sseController.sendStatusUpdate("refresh");
        }
        
        
    }
	
	
    // ===== 每天凌晨 00:05 執行Reservation 補空白排程 =====
	//只補今天至兩個月後的缺口，已存在的資料完全不碰
//	@Scheduled(cron = "0 5 0 * * ?")   // 每天 00:05
    @Scheduled(fixedRate = 600_000)   // 測試用
    public void fillMissingReservations() {

        LocalDate today      = LocalDate.now();
        LocalDate oneMonths  = today.plusMonths(1);

        List<RestoVO> restos = restoRepo.findAll();          // 取全部餐廳
        List<TimeslotVO> allSlots = timeslotRepo.findAll();  // 取全部時段

        for (LocalDate d = today; !d.isAfter(oneMonths); d = d.plusDays(1)) {

            for (RestoVO resto : restos) {

                // 取出隸屬此餐廳的時段  
                List<TimeslotVO> timeslots = timeslotRepo.findByRestoVO_RestoIdAndIsDeletedFalseOrderByTimeslotNameAsc(resto.getRestoId());

                for (TimeslotVO slot : timeslots) {

                    boolean exists = reservationRepo
                        .existsByRestoVO_RestoIdAndReserveDateAndReserveTimeslotVO_TimeslotId(
                             resto.getRestoId(), d, slot.getTimeslotId());

                    if (exists) {
                        continue;   // 已有資料 → 不動
                    }

                    /* ----------- 補一筆空白 Reservation ----------- */
                    RestoReservationVO vo = new RestoReservationVO();
                    vo.setRestoVO(resto);
                    vo.setTimeslotVO(slot);
                    vo.setReserveDate(d);
                    vo.setRestoSeatsTotal(resto.getRestoSeatsTotal());  // 座位上限
                    // reserveSeatsTotal 預設 0

                    reservationRepo.save(vo);
                }
            }
        }
    }
	
	
	
	
	
	
	
	
	
	
    
    
    
    
    
    
    
}
	
	


