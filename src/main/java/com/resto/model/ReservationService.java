package com.resto.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.RestoReservationVO;
import com.resto.utils.RestoRsvtCriteriaHelper;
import com.resto.utils.exceptions.OverbookingException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ReservationService {

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    RestoRepository restoRepository;
    @Autowired
    TimeslotRepository timeslotRepository;
    
    // 複合查詢（Criteria 結構）
    @Transactional(readOnly = true)
    public List<RestoReservationVO> compositeQuery(Map<String, String[]> paramMap) {
        return RestoRsvtCriteriaHelper.getAll(paramMap, em);
    } 

    
    // 隨餐廳編輯更新預約表的容納人數
    @Transactional
    public int refreshSeatsTotal(Integer restoId, int newTotal) {
        return reservationRepository.updateSeatsTotalByResto(restoId, newTotal);
    }
    
//    //取出、建立當天預約vo
//    @Transactional
//    private RestoReservationVO getEntry(Integer restoId,
//                                        Integer timeslotId,
//                                        LocalDate date) {
//
//        return reservationRepository
//            .findByRestoVO_RestoIdAndReserveDateAndReserveTimeslotVO_TimeslotId(
//                    restoId, date, timeslotId)
//            .orElseGet(() -> {  // 若無訂單存在就建一空的
//                RestoReservationVO vo = new RestoReservationVO();
//                
//                vo.setRestoVO(restoRepository.getReferenceById(restoId));
//                //用 repository 提供的 reference / 實體，讓整個 Session 中只有「同一份」物件
//                vo.setTimeslotVO(timeslotRepository.getReferenceById(timeslotId));
//                vo.setReserveDate(date);
//                vo.setRestoSeatsTotal(vo.getRestoVO().getRestoSeatsTotal());
//                // reserveSeatsTotal已在vo預設0
//                
//                return reservationRepository.save(vo);
//            });
//    }
    
    
    /** 只查不寫，Optional 沒找到就回 empty。 */
    @Transactional(readOnly = true)
    private Optional<RestoReservationVO> findEntry(
            Integer restoId, Integer timeslotId, LocalDate date) {

        return reservationRepository
                .findByRestoVO_RestoIdAndReserveDateAndReserveTimeslotVO_TimeslotId(
                        restoId, date, timeslotId);
    }

    /** 真正需要寫入時才呼叫，沒有就 create & save。 */
    @Transactional
    private RestoReservationVO ensureEntry(
            Integer restoId, Integer timeslotId, LocalDate date) {

        return findEntry(restoId, timeslotId, date)
                .orElseGet(() -> {
                    RestoReservationVO vo = new RestoReservationVO();
                    vo.setRestoVO(   restoRepository   .getReferenceById(restoId));
                    vo.setTimeslotVO(timeslotRepository.getReferenceById(timeslotId));
                    vo.setReserveDate(date);
                    vo.setRestoSeatsTotal(vo.getRestoVO().getRestoSeatsTotal());
                    return reservationRepository.save(vo);
                });
    }

    
    
    



    // ==== 需要增減座位 ====
    // 更新(釋放/占用)每時段實際預約人數
    @Transactional
    public void adjustSeats(Integer restoId, Integer timeslotId,
            LocalDate date, int diff,Integer currentOrderSeats) {
		
    	if (diff == 0) return;
		
        RestoReservationVO entry = ensureEntry(restoId, timeslotId, date);
		
		
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

    
 // ==== 純查詢 ====
    @Transactional(readOnly = true)
    public int getRemaining(Integer rid, Integer tsid, LocalDate d) {
        return findEntry(rid, tsid, d)
               .map(e -> e.getRestoSeatsTotal() - e.getReserveSeatsTotal())
               .orElse(restoRepository.getReferenceById(rid).getRestoSeatsTotal());
    }

    @Transactional(readOnly = true)
    public int getReserved(Integer rid, Integer tsid, LocalDate d) {
        return findEntry(rid, tsid, d)
               .map(RestoReservationVO::getReserveSeatsTotal)
               .orElse(0);
    }
   
    
    // 前台訂單佔位
    @Transactional
    public void occupy(Integer restoId, Integer timeslotId,
                       LocalDate date, int seats) {

        // 確保有那一比reservation統計row（若同時 insert 有 race，這裡用 ensureEntry 放在同一 Tx 仍安全）
        ensureEntry(restoId, timeslotId, date);

        int updated = reservationRepository
                        .addSeatsIfAvailable(restoId, timeslotId, date, seats);

        if (updated == 0) {
            throw new OverbookingException("訂位名額產生異動，該時段已無足夠座位，請選擇其他日期、時段");
        }
    }
    
    
    
    
    
    
    
    
    
    
    
}

    
    
    
    
    
    
    
    
    
    