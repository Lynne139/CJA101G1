package com.resto.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
import com.resto.utils.RestoOrderStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class RestoOrderService {

    @PersistenceContext
    private EntityManager em;

    private final RestoOrderRepository restoOrderRepository;
    private final RestoRepository restoRepository;
    private final TimeslotRepository timeslotRepository;

    public RestoOrderService(RestoOrderRepository restoOrderRepository, RestoRepository restoRepository,TimeslotRepository timeslotRepository) {
        this.restoOrderRepository = restoOrderRepository;
        this.restoRepository = restoRepository;
        this.timeslotRepository = timeslotRepository;
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


    	restoOrderRepository.save(restoOrderVO);
    }
    
    
    // 更新入資料庫
    @Transactional
    public void update(RestoOrderVO restoOrderVO) {
    	restoOrderRepository.save(restoOrderVO);
    }
   
    
    
    // 可選日期
    public List<LocalDate> getAvailableDates(Integer restoId) {
        RestoVO resto = restoRepository.findById(restoId).orElseThrow();
        int totalSeats = resto.getRestoSeatsTotal();

        // 查出每一天的總訂位數量
        List<Object[]> results = restoOrderRepository.findBookedSeatsPerDate(restoId);

        // 計算從今天起後一個月內的所有日期
        List<LocalDate> allDates = IntStream.range(0, 30)
            .mapToObj(i -> LocalDate.now().plusDays(i))
            .collect(Collectors.toList());

        // 建立已訂座數 map
        Map<LocalDate, Long> bookedMap = results.stream()
            .collect(Collectors.toMap(r -> (LocalDate) r[0], r -> (Long) r[1]));

        // 回傳未滿額的日期
        return allDates.stream()
            .filter(date -> bookedMap.getOrDefault(date, 0L) < totalSeats)
            .collect(Collectors.toList());
    }

    
    // disable日期
    public List<LocalDate> getFullyBookedDates(Integer restoId) {
        RestoVO resto = restoRepository.findById(restoId).orElseThrow();
        int totalSeats = resto.getRestoSeatsTotal();

        List<Object[]> booked = restoOrderRepository.findBookedSeatsPerDate(
                restoId, RestoOrderStatus.CANCELED);

        return booked.stream()
                     .filter(arr -> ((Long) arr[1]) >= totalSeats)   // 滿額
                     .map(arr -> (LocalDate) arr[0])
                     .toList();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
