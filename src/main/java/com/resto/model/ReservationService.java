package com.resto.model;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.RestoReservationVO;
import com.resto.entity.TimeslotVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
//@RequiredArgsConstructor
public class ReservationService {

    @PersistenceContext
    private EntityManager em;

    private final ReservationRepository reservationRepository;
    private final RestoRepository restoRepository;


    public ReservationService(ReservationRepository reservationRepository,RestoRepository restoRepository) {
        this.reservationRepository = reservationRepository;
        this.restoRepository = restoRepository;
    }

    
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
                
                return reservationRepository.save(vo);
            });
    }

    // 當日剩餘名額
    public int getRemaining(Integer restoId, Integer timeslotId, LocalDate date){
        var entry = getEntry(restoId,timeslotId,date);
        return Math.max(entry.getRestoSeatsTotal()
                     - entry.getReserveSeatsTotal(), 0);
    }

    // 更新預約狀況，防呆預約人數若超額丟 IllegalStateException
    @Transactional
    public void reserve(Integer restoId, Integer timeslotId,
                        LocalDate date, int seats){

        var entry  = getEntry(restoId,timeslotId,date);
        int remain = entry.getRestoSeatsTotal() - entry.getReserveSeatsTotal();
        if (seats > remain) {
            throw new IllegalStateException("剩餘 " + remain + " 位，名額不足");
        }
        entry.setReserveSeatsTotal(entry.getReserveSeatsTotal() + seats);
        // JPA 內部自動 flush
    }
    
    
    // 更新(釋放/占用)每時段實際預約人數
    @Transactional
    public void adjust(Integer restoId,Integer timeslotId,
                       LocalDate date,int deltaSeats){

        var entry = getEntry(restoId,timeslotId,date);
        int newTotal = entry.getReserveSeatsTotal() + deltaSeats;

        if (newTotal < 0)
            throw new IllegalStateException("reserveSeatsTotal < 0 ?!");

        if (newTotal > entry.getRestoSeatsTotal())
            throw new IllegalStateException("剩餘名額不足");

        entry.setReserveSeatsTotal(newTotal);
    }

    
    
    
    
    
    
    
    
    
    
}

    
    
    
    
    
    
    
    
    
    