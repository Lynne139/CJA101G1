package com.resto.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.resto.entity.RestoVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ReservationService {

    @PersistenceContext
    private EntityManager em;

    private final ReservationRepository reservationRepository;
    private final RestoRepository restoRepository;
    private final RestoOrderRepository restoOrderRepository;

    public ReservationService(ReservationRepository reservationRepository,RestoRepository restoRepository,RestoOrderRepository restoOrderRepository) {
        this.reservationRepository = reservationRepository;
        this.restoRepository = restoRepository;
        this.restoOrderRepository = restoOrderRepository;
    }

    public List<LocalDate> getFullBookedDates(Integer restoId) {
        RestoVO resto = restoRepository.findById(restoId).orElseThrow();
        int totalSeats = resto.getRestoSeatsTotal();

        // 自訂查詢每個日期已訂人數
        List<Object[]> result = restoOrderRepository.findBookedSeatsPerDate(restoId);

        List<LocalDate> fullDates = new ArrayList<>();
        for (Object[] row : result) {
            LocalDate date = (LocalDate) row[0];
            Long seatsBooked = (Long) row[1];
            if (seatsBooked >= totalSeats) {
                fullDates.add(date);
            }
        }
        return fullDates;
    }
    
    
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

    
    
    
    
    
    
    
    
    
    
    
    
}

    
    
    
    
    
    
    
    
    
    

