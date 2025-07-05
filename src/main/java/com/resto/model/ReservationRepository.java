package com.resto.model;


import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resto.entity.RestoReservationVO;

@Repository
public interface ReservationRepository extends JpaRepository<RestoReservationVO, Integer>  {

	// 指定餐廳+日期+時段的資料(每日預約vo)
	Optional<RestoReservationVO>
	findByRestoVO_RestoIdAndReserveDateAndReserveTimeslotVO_TimeslotId(
	        Integer restoId, LocalDate date, Integer timeslotId);
	
	
	// 預約表的餐廳容納人數隨餐廳的編輯更新
	@Modifying
	@Query("""
	      UPDATE RestoReservationVO r
	         SET r.restoSeatsTotal = :newTotal
	       WHERE r.restoVO.restoId = :restoId
	       AND r.reserveDate >= CURRENT_DATE
	""")
	int updateSeatsTotalByResto(@Param("restoId") Integer restoId,
	                             @Param("newTotal") Integer newTotal);

	
	
	
}