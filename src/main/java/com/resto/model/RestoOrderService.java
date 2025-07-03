package com.resto.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.dto.RestoOrderDTO;
import com.resto.entity.PeriodVO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.utils.RestoOrderCriteriaHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class RestoOrderService {

    @PersistenceContext
    private EntityManager em;

    private final RestoOrderRepository restoOrderRepository;
    private final RestoRepository restoRepository;
    private final TimeslotRepository timeslotRepository;
    private final ReservationService reservationService;
    private final RestoService restoService;
    private final TimeslotService timeslotService;

    public RestoOrderService(RestoOrderRepository restoOrderRepository, 
    		RestoRepository restoRepository,
    		TimeslotRepository timeslotRepository,
    		ReservationService reservationService,
    		RestoService restoService,
    		TimeslotService timeslotService) {
        this.restoOrderRepository = restoOrderRepository;
        this.restoRepository = restoRepository;
        this.timeslotRepository = timeslotRepository;
        this.reservationService = reservationService;
        this.restoService = restoService;
        this.timeslotService = timeslotService;
    }
    

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

        // 檢查(剩餘名額>=這筆訂單的人數)並佔位(若名額足夠就先把 reserveSeatsTotal 加上去，避免多個管理員同時下單時超額)
        reservationService.reserve(
        		restoOrderVO.getRestoVO().getRestoId(),
        		restoOrderVO.getTimeslotVO().getTimeslotId(),
        		restoOrderVO.getRegiDate(),
        		restoOrderVO.getRegiSeats()
        );
        
        
    	restoOrderRepository.save(restoOrderVO);
    }
    
    
    // 更新入資料庫
//    @Transactional
//    public void update(RestoOrderVO restoOrderVO) {
//    	RestoOrderVO original = restoOrderRepository.findById(restoOrderVO.getRestoOrderId())
//    	        .orElseThrow(() -> new EntityNotFoundException("找不到訂單"));
//
//    	    // 只覆寫允許修改的欄位
//    	    original.setOrderStatus(restoOrderVO.getOrderStatus());
//    	    original.setRestoVO(restoService.getById(restoOrderVO.getRestoVO().getRestoId()));
//    	    original.setTimeslotVO(timeslotService.getById(restoOrderVO.getTimeslotVO().getTimeslotId()));
//    	    original.setRegiDate(restoOrderVO.getRegiDate());
//    	    original.setRegiSeats(restoOrderVO.getRegiSeats());
//    	    original.setHighChairs(restoOrderVO.getHighChairs());
//    	    original.setRegiReq(restoOrderVO.getRegiReq());
//    	    original.setOrderGuestName(restoOrderVO.getOrderGuestName());
//    	    original.setOrderGuestPhone(restoOrderVO.getOrderGuestPhone());
//    	    original.setOrderGuestEmail(restoOrderVO.getOrderGuestEmail());
//    	    original.setAdminNote(restoOrderVO.getAdminNote());
//    	    }
    @Transactional
    public void update(RestoOrderVO vo){

        RestoOrderVO original = restoOrderRepository.findById(vo.getRestoOrderId())
            .orElseThrow(() -> new EntityNotFoundException("找不到訂單"));

        //比較日期+時段+餐廳 跟 新送出的 是否有變
        boolean keyChanged =
               !original.getRegiDate().equals(vo.getRegiDate()) ||
               !original.getTimeslotVO().getTimeslotId()
                       .equals(vo.getTimeslotVO().getTimeslotId()) ||
               !original.getRestoVO().getRestoId()
                       .equals(vo.getRestoVO().getRestoId());

        // 若改變，且 用餐人數 有無變化
        if (keyChanged || !original.getRegiSeats().equals(vo.getRegiSeats())) {

            // 釋放舊的
            reservationService.adjust(
                original.getRestoVO().getRestoId(),
                original.getTimeslotVO().getTimeslotId(),
                original.getRegiDate(),
                -original.getRegiSeats());              //負值 = 釋放

            // 佔用新的
            reservationService.adjust(
                vo.getRestoVO().getRestoId(),
                vo.getTimeslotVO().getTimeslotId(),
                vo.getRegiDate(),
                +vo.getRegiSeats());
        }

        // 重新抓關聯（用getReference避免多次查）
        original.setRestoVO(em.getReference(RestoVO.class,
                         vo.getRestoVO().getRestoId()));
        original.setTimeslotVO(em.getReference(TimeslotVO.class,
                         vo.getTimeslotVO().getTimeslotId()));

        // 其他填寫欄位直接覆寫
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
    }

   
    
    
//    // 可選日期
//    public List<LocalDate> getAvailableDates(Integer restoId) {
//        RestoVO resto = restoRepository.findById(restoId).orElseThrow();
//        int totalSeats = resto.getRestoSeatsTotal();
//
//        // 查出每一天的總訂位數量
//        List<Object[]> results = restoOrderRepository.findBookedSeatsPerDate(restoId);
//
//        // 計算從今天起後一個月內的所有日期
//        List<LocalDate> allDates = IntStream.range(0, 30)
//            .mapToObj(i -> LocalDate.now().plusDays(i))
//            .collect(Collectors.toList());
//
//        // 建立已訂座數 map
//        Map<LocalDate, Long> bookedMap = results.stream()
//            .collect(Collectors.toMap(r -> (LocalDate) r[0], r -> (Long) r[1]));
//
//        // 回傳未滿額的日期
//        return allDates.stream()
//            .filter(date -> bookedMap.getOrDefault(date, 0L) < totalSeats)
//            .collect(Collectors.toList());
//    }

  
    
    
    
    
    
    
    
    
    
}