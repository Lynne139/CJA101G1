package com.resto.model;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resto.entity.RestoReservationVO;
import com.resto.entity.TimeslotVO;

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

    
    //取出、建立當天預約vo
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
    public int getRemaining(Integer restoId, Integer timeslotId, LocalDate date){
        var entry = getEntry(restoId,timeslotId,date);
        return Math.max(entry.getRestoSeatsTotal()
                     - entry.getReserveSeatsTotal(), 0);
    }

    
    // 更新(釋放/占用)每時段實際預約人數
    public void adjustSeats(Integer restoId, Integer timeslotId,
            LocalDate date, int diff) {
		
    	if (diff == 0) return;
		
		RestoReservationVO entry = getEntry(restoId, timeslotId, date);
		// 正diff 代表占位，負diff 代表釋放
		int newTotal = entry.getReserveSeatsTotal() + diff;
		
		// 保底 0、保頂 restoSeatsTotal
		newTotal = Math.max(0, Math.min(entry.getRestoSeatsTotal(), newTotal));
		entry.setReserveSeatsTotal(newTotal);
		
		// reservationRepo.save(entry);可省略，Entity 已託管
	}

    
    
    
    
    
    
    
    
    
    
}

    
    
    
    
    
    
    
    
    
    