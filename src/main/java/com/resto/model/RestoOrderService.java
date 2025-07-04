package com.resto.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.dto.RestoOrderDTO;
import com.resto.entity.PeriodVO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.utils.RestoOrderCriteriaHelper;
import com.resto.utils.RestoOrderStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class RestoOrderService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private RestoOrderRepository restoOrderRepository;
    @Autowired
    private RestoRepository restoRepository;
    @Autowired
    private TimeslotRepository timeslotRepository;
    @Autowired
    private ReservationService reservationService;
    

    // Datatable顯示複合查詢結果
    @Transactional(readOnly = true)
    public List<RestoOrderDTO> compositeQueryAsDTO(Map<String,String[]> param) {
        return RestoOrderCriteriaHelper.getAllDTO(param, em);
    }
    
	// id拿單筆
    @Transactional(readOnly = true)
    public RestoOrderVO getById(Integer restoOrderId) {
        return restoOrderRepository.findById(restoOrderId).orElse(null);
    }
    
    // 同步更新預約佔位
    private void syncReservationSeats(RestoOrderVO before, RestoOrderVO after) {
    	// 安全取值(null)
    	boolean beforePresent = before != null;
        boolean afterPresent  = after  != null;
    	
    	// 判斷訂單狀態是否佔位(enum countable>成立、保留；!countable>取消、逾期、完成)
        boolean beforeCounts = beforePresent && before.getOrderStatus().isCountable();
        boolean afterCounts  = afterPresent  && after.getOrderStatus().isCountable();

        // 同一預定槽訂單(同餐廳、時段、日期)
        boolean sameSlot = false;
        if (beforePresent && afterPresent) {
            sameSlot =
                Objects.equals(before.getRestoVO().getRestoId(),      after.getRestoVO().getRestoId()) &&
                Objects.equals(before.getTimeslotVO().getTimeslotId(), after.getTimeslotVO().getTimeslotId()) &&
                Objects.equals(before.getRegiDate(), after.getRegiDate());
        }

        // 訂單從 佔位 → 佔位
        if (beforeCounts && afterCounts) {

            if (sameSlot) {
                // 同一 slot 只需做 名額占用+/-
                int diff = after.getRegiSeats() - before.getRegiSeats();
                if (diff != 0) {
                    reservationService.adjustSeats(
                        after.getRestoVO().getRestoId(),
                        after.getTimeslotVO().getTimeslotId(),
                        after.getRegiDate(),
                        diff);
                }
            } else {
                // 換了 slot，先釋放舊，再占用新
                reservationService.adjustSeats(
                    before.getRestoVO().getRestoId(),
                    before.getTimeslotVO().getTimeslotId(),
                    before.getRegiDate(),
                    -before.getRegiSeats());

                reservationService.adjustSeats(
                    after.getRestoVO().getRestoId(),
                    after.getTimeslotVO().getTimeslotId(),
                    after.getRegiDate(),
                    after.getRegiSeats());
            }

        // 從佔位 → 不佔位
        } else if (beforeCounts && !afterCounts) {
            reservationService.adjustSeats(
                before.getRestoVO().getRestoId(),
                before.getTimeslotVO().getTimeslotId(),
                before.getRegiDate(),
                -before.getRegiSeats());

        // 從不佔位 → 佔位 
        } else if (!beforeCounts && afterCounts) {
            reservationService.adjustSeats(
                after.getRestoVO().getRestoId(),
                after.getTimeslotVO().getTimeslotId(),
                after.getRegiDate(),
                after.getRegiSeats());
        }
    }

    
    // 刪除
    @Transactional
    public void deleteById(Integer orderId) {

        // 取出原資料
        RestoOrderVO order = restoOrderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("找不到訂單"));

        // 同步更新預約佔位
        syncReservationSeats(order, null);

        restoOrderRepository.delete(order);
    }


    // 新增入資料庫
    @Transactional
    public void insert(RestoOrderVO restoOrderVO) {
	
    	// 確保關聯對象完整
        RestoVO resto = restoRepository.findById(restoOrderVO.getRestoVO().getRestoId()).orElseThrow();
        TimeslotVO timeslot = timeslotRepository.findById(restoOrderVO.getTimeslotVO().getTimeslotId()).orElseThrow();
        PeriodVO period = timeslot.getPeriodVO();

        restoOrderVO.setRestoVO(resto);
        restoOrderVO.setTimeslotVO(timeslot);
        
        // 自動補 snapshot 欄位
        restoOrderVO.setSnapshotRestoName(resto.getRestoName());
        restoOrderVO.setSnapshotRestoNameEn(resto.getRestoNameEn());
        restoOrderVO.setSnapshotPeriodName(period.getPeriodName());
        restoOrderVO.setSnapshotTimeslotName(timeslot.getTimeslotName());
        
        // 記錄下單時間
        restoOrderVO.setOrderTime(LocalDateTime.now());

        // 補逾期時間（假設時段名稱為 HH:mm）
        LocalTime slotTime = LocalTime.parse(timeslot.getTimeslotName()); // "18:00"
        restoOrderVO.setReserveExpireTime(restoOrderVO.getRegiDate().atTime(slotTime).plusMinutes(10));

        RestoOrderVO saved = restoOrderRepository.save(restoOrderVO);
        // 同步更新預約佔位
        syncReservationSeats(null, saved);
    }
    
    
    // 更新入資料庫
    @Transactional
    public void update(RestoOrderVO vo){

        RestoOrderVO original = restoOrderRepository.findById(vo.getRestoOrderId())
            .orElseThrow(() -> new EntityNotFoundException("找不到訂單"));

     
        // 建立 before 快照（只保留會影響名額的欄位）
        RestoOrderVO before = new RestoOrderVO();
        before.setRestoVO(em.getReference(RestoVO.class, original.getRestoVO().getRestoId()));
        before.setTimeslotVO(em.getReference(TimeslotVO.class, original.getTimeslotVO().getTimeslotId()));
        before.setRegiDate(original.getRegiDate());
        before.setRegiSeats(original.getRegiSeats());
        before.setOrderStatus(original.getOrderStatus());
  
        
        // 覆寫original的填寫欄位
        original.setRestoVO(em.getReference(RestoVO.class, vo.getRestoVO().getRestoId()));
        original.setTimeslotVO(em.getReference(TimeslotVO.class, vo.getTimeslotVO().getTimeslotId()));
        original.setOrderStatus(vo.getOrderStatus());
        original.setRegiDate(vo.getRegiDate());
        original.setRegiSeats(vo.getRegiSeats());
        original.setHighChairs(vo.getHighChairs());
        original.setRegiReq(vo.getRegiReq());
        original.setOrderGuestName(vo.getOrderGuestName());
        original.setOrderGuestPhone(vo.getOrderGuestPhone());
        original.setOrderGuestEmail(vo.getOrderGuestEmail());
        original.setAdminNote(vo.getAdminNote());

        // 重新填 snapshot & expire
        TimeslotVO ts = original.getTimeslotVO();
        RestoVO     r  = original.getRestoVO();

        original.setSnapshotRestoName( r.getRestoName() );
        original.setSnapshotRestoNameEn( r.getRestoNameEn() );
        original.setSnapshotPeriodName( ts.getPeriodVO().getPeriodName() );
        original.setSnapshotTimeslotName( ts.getTimeslotName() );

        LocalTime slotTime = LocalTime.parse(ts.getTimeslotName());
        original.setReserveExpireTime(
            original.getRegiDate().atTime(slotTime).plusMinutes(10));
        
        
        // 同步更新預約佔位
        syncReservationSeats(before, original);
        
    }

    
    // 隨住宿訂單取消
    @Transactional
    public void cancelByRoomOrderId(Integer roomOrderId) {
        List<RestoOrderVO> orders = restoOrderRepository.findByRoomOrder_RoomOrderId(roomOrderId);
        for (RestoOrderVO order : orders) {
            order.setOrderStatus(RestoOrderStatus.CANCELED);
        }
    }
    
   
    // 今日訂單切換訂單狀態
    @Transactional
    public RestoOrderStatus toggleStatus(Integer id, RestoOrderStatus newStatus){
        RestoOrderVO order = restoOrderRepository.findById(id).orElseThrow();

        RestoOrderVO snapshot = new RestoOrderVO();   // 只放需要比對的欄位
        snapshot.setRegiSeats(order.getRegiSeats());
        snapshot.setOrderStatus(order.getOrderStatus());
        snapshot.setRestoVO(order.getRestoVO());
        snapshot.setTimeslotVO(order.getTimeslotVO());
        snapshot.setRegiDate(order.getRegiDate());

        order.setOrderStatus(newStatus);
        // 同步更新預約佔位
        syncReservationSeats(snapshot, order);
        return newStatus;
    }
    
    
    // 找出今日訂單
    public List<RestoOrderVO> findTodayOrders(Integer restoId) {
        return restoOrderRepository.findTodayOrders(restoId);
    }
    
    

  
    
    
    
    
    
    
    
    
    
}