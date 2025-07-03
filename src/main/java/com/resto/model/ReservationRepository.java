package com.resto.model;


import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resto.entity.RestoReservationVO;

public interface ReservationRepository extends JpaRepository<RestoReservationVO, Integer>  {

	// 指定餐廳+日期+時段的資料(每日預約vo)
	Optional<RestoReservationVO>
	findByRestoVO_RestoIdAndReserveDateAndReserveTimeslotVO_TimeslotId(
	        Integer restoId, LocalDate date, Integer timeslotId);

	
	
	
}