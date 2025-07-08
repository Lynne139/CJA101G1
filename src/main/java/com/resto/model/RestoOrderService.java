package com.resto.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.resto.controller.SseController;
import com.resto.dto.RestoOrderDTO;
import com.resto.dto.RestoOrderSummaryDTO;
import com.resto.entity.PeriodVO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.utils.RestoOrderCriteriaHelper;
import com.resto.utils.RestoOrderSource;
import com.resto.utils.RestoOrderStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class RestoOrderService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    RestoOrderRepository restoOrderRepository;
    @Autowired
    RestoRepository restoRepository;
    @Autowired
    TimeslotRepository timeslotRepository;
    @Autowired
    ReservationService reservationService;
    
    @Autowired
    private SseController sseController;
    

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
                // 同一 timeslot 只需做 名額占用+/-
                int diff = after.getRegiSeats() - before.getRegiSeats();
                if (diff != 0) {
                    reservationService.adjustSeats(
                        after.getRestoVO().getRestoId(),
                        after.getTimeslotVO().getTimeslotId(),
                        after.getRegiDate(),
                        diff,
                    	before.getRegiSeats()); // 本筆原始人數
                }
            } else {
                // 換了 slot，先釋放舊，再占用新
                reservationService.adjustSeats(
                    before.getRestoVO().getRestoId(),
                    before.getTimeslotVO().getTimeslotId(),
                    before.getRegiDate(),
                    -before.getRegiSeats(),
                    null); // 釋放不需 ownSeats

                reservationService.adjustSeats(
                    after.getRestoVO().getRestoId(),
                    after.getTimeslotVO().getTimeslotId(),
                    after.getRegiDate(),
                    after.getRegiSeats(),
                    0); // 新增時原本佔位 0
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

        
        // 若為 ROOM 訂單，且尚未被取消，才通知住宿系統
        if (order.getOrderSource() == RestoOrderSource.ROOM &&
            order.getOrderStatus() != RestoOrderStatus.CANCELED &&
            order.getRoomOrder() != null) {

//            callCancelRoomProjectApi(order.getRoomOrder().getRoomOrderId());
        }
         
        // 同步更新預約佔位
        syncReservationSeats(order, null);

        restoOrderRepository.delete(order);
        
        sseController.sendStatusUpdate("refresh");
        
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
        
        sseController.sendStatusUpdate("refresh");
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
        em.flush();
        
        boolean becameCanceled =
                before.getOrderStatus() != RestoOrderStatus.CANCELED &&
                original.getOrderStatus() == RestoOrderStatus.CANCELED;

        if (becameCanceled &&
            original.getOrderSource() == RestoOrderSource.ROOM &&
            original.getRoomOrder()   != null) {

            Integer roomOrderId = original.getRoomOrder().getRoomOrderId();
            callCancelRoomProjectApi(roomOrderId); 
            
        }

        sseController.sendStatusUpdate("refresh");
        
        
    }

    
    // 跟隨住宿訂單被取消
    @Transactional
    public void cancelByRoomOrderId(Integer roomOrderId) {
        List<RestoOrderVO> orders = restoOrderRepository.findByRoomOrder_RoomOrderId(roomOrderId);
        for (RestoOrderVO order : orders) {
            order.setOrderStatus(RestoOrderStatus.CANCELED);
        }
    }
    
   
    // 今日訂單切換訂單狀態
    @Transactional
    public Map<String, Object> toggleStatus(Integer id, RestoOrderStatus newStatus) {
        RestoOrderVO order = restoOrderRepository.findById(id).orElseThrow();

        // 備份必要欄位（用於座位釋出判斷）
        RestoOrderVO snapshot = new RestoOrderVO();
        snapshot.setRegiSeats(order.getRegiSeats());
        snapshot.setOrderStatus(order.getOrderStatus());
        snapshot.setRestoVO(order.getRestoVO());
        snapshot.setTimeslotVO(order.getTimeslotVO());
        snapshot.setRegiDate(order.getRegiDate());

        // 狀態更新
        order.setOrderStatus(newStatus);
        syncReservationSeats(snapshot, order); // 自動釋出座位

        // 查詢最新統計
        RestoOrderSummaryDTO summary = restoOrderRepository.findTodaySummary(order.getRestoVO().getRestoId());

        // 回傳資訊給 controller → JS
        Map<String, Object> result = new HashMap<>();
        result.put("label", newStatus.getLabel());
        result.put("cssClass", newStatus.getCssClass());
        result.put("summary", summary);
        return result;
    }

    
    
    // 找出今日訂單
    public List<RestoOrderVO> findTodayOrders(Integer restoId) {
        return restoOrderRepository.findTodayOrders(restoId);
    }
    
    
    //  特定餐廳每日訂單的統計
    public RestoOrderSummaryDTO getTodaySummary(Integer restoId) {
        return restoOrderRepository.findTodaySummary(restoId);
    }
    
    //餐廳加總每日訂單統計
    public List<RestoOrderSummaryDTO> getAllTodaySummaryPerResto() {
        return restoOrderRepository.findAllTodaySummaryPerResto();
    }




    
    
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ROOM_API_URL = "http://localhost:8080/room/cancel_project_add_on"; // 先用本機開發用的網址

    // 呼叫住宿系統的 Controller API 取消加購專案
    private void callCancelRoomProjectApi(Integer roomOrderId) {
        try {
            // 設定請求的 Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 建立 JSON 結構 { "roomOrderId": 123 }
            Map<String, Object> body = new HashMap<>();
            body.put("roomOrderId", roomOrderId);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 發出 POST 請求
            ResponseEntity<String> response = restTemplate.postForEntity(
                ROOM_API_URL,
                requestEntity,
                String.class
            );

            // 檢查回應狀態
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("通知住宿取消專案成功：" + roomOrderId);
            } else {
                System.err.println("通知住宿取消專案失敗，回應：" + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("呼叫住宿取消專案失敗: " + e.getMessage());
        }
    }

    
    // 前台嘗試定位(驗證名額)
    @Transactional
    public void tryCreateOrder(RestoOrderVO order) {

        // 先嘗試原子占位
        reservationService.occupy(
            order.getRestoVO().getRestoId(),
            order.getTimeslotVO().getTimeslotId(),
            order.getRegiDate(),
            order.getRegiSeats()
        );

        // 占位成功，insert()+syncReservationSeats()
        insert(order); 
    }
    

    
    
    
}