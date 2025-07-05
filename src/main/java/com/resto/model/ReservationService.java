package com.resto.model;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.RestoReservationVO;
import com.resto.entity.TimeslotVO;
import com.resto.utils.exceptions.OverbookingException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ReservationService {

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RestoRepository restoRepository;
    
    
    
    
    
    // 隨餐廳編輯更新預約表的容納人數
    @Transactional
    public int refreshSeatsTotal(Integer restoId, int newTotal) {
        return reservationRepository.updateSeatsTotalByResto(restoId, newTotal);
    }
    
    
    
    

    
    //取出、建立當天預約vo
    @Transactional
    private RestoReservationVO getEntry(Integer restoId,
                                        Integer timeslotId,
                                        LocalDate date) {

        return reservationRepository
            .findByRestoVO_RestoIdAndReserveDateAndReserveTimeslotVO_TimeslotId(
                    restoId, date, timeslotId)
            .orElseGet(() -> {  // 若無訂單存在就建一空的
                RestoReservationVO vo = new RestoReservationVO();
                
                vo.setRestoVO(restoRepository.getReferenceById(restoId));
                vo.setTimeslotVO(new TimeslotVO(timeslotId)); // 只需 idproxy
                vo.setReserveDate(date);
                vo.setRestoSeatsTotal(vo.getRestoVO().getRestoSeatsTotal());
                // reserveSeatsTotal已在vo預設0
                
                return reservationRepository.save(vo);
            });
    }

    // 當日剩餘名額
    @Transactional
    public int getRemaining(Integer restoId, Integer timeslotId, LocalDate date){
        var entry = getEntry(restoId,timeslotId,date);
        return Math.max(entry.getRestoSeatsTotal()
                     - entry.getReserveSeatsTotal(), 0);
    }

    
    // 更新(釋放/占用)每時段實際預約人數
    @Transactional
    public void adjustSeats(Integer restoId, Integer timeslotId,
            LocalDate date, int diff,Integer currentOrderSeats) {
		
    	if (diff == 0) return;
		
		RestoReservationVO entry = getEntry(restoId, timeslotId, date);
		
		
		int limit    = entry.getRestoSeatsTotal();      // 上限 230
	    int reserved = entry.getReserveSeatsTotal();    // 時段總量（含本筆）
	    int requested = reserved + diff;                // 調整後總量
	    // 正diff 代表占位，負diff 代表釋放

		
		// 超賣判斷
	    if (requested > limit) {
	        // 取用傳進來的「本筆原始人數」
	        int ownSeats = (currentOrderSeats != null)
	                         ? currentOrderSeats
	                         : reserved;   // null時退回總量，for safety

	        throw new OverbookingException(
	            "超過餐廳座位上限！目前上限 "
	            + limit +
	            "，本訂單原已預約 "
	            + ownSeats +
	            "，僅可保留原人數或減少");
	    }
		
	    entry.setReserveSeatsTotal(requested);

		// reservationRepo.save(entry);可省略，Entity 已託管
	}
    // 給新增用的overload
    @Transactional
    public void adjustSeats(Integer restoId,
            Integer timeslotId,
            LocalDate date,
            int diff) {
    		adjustSeats(restoId, timeslotId, date, diff, null);
    }

    
    // timeslot總佔位
    public int getReserved(Integer restoId, Integer timeslotId, LocalDate date) {
        return getEntry(restoId, timeslotId, date).getReserveSeatsTotal();
    }
    
    
    
    
    
    
    
    
    
    
}

    
    
    
    
    
    
    
    
    
    