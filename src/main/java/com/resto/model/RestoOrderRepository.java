package com.resto.model;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resto.entity.RestoOrderVO;
import com.resto.utils.RestoOrderStatus;

@Repository
public interface RestoOrderRepository extends JpaRepository<RestoOrderVO, Integer>  {

//		// 每日該餐廳總訂位
//		@Query("""
//		    SELECT ro.regiDate, SUM(ro.regiSeats)
//		    FROM RestoOrderVO ro
//		    WHERE ro.restoVO.restoId = :restoId 
//		      AND ro.orderStatus <> com.resto.utils.RestoOrderStatus.CANCELED
//		    GROUP BY ro.regiDate
//		""")
//		List<Object[]> findBookedSeatsPerDate(@Param("restoId") Integer restoId);

	
	
	// 抓特定狀態的訂單
//	List<RestoOrderVO> findByOrderStatus(RestoOrderStatus status);
	
	// 抓今天特定狀態的訂單
	@Query("FROM RestoOrderVO o WHERE o.regiDate = :today AND o.orderStatus = :status")
	List<RestoOrderVO> findTodayOrdersByOrderStatus(@Param("today") LocalDate today,
	                                          @Param("status") RestoOrderStatus status);

	// 以住宿訂單抓餐廳訂單
	List<RestoOrderVO> findByRoomOrder_RoomOrderId(Integer roomOrderId);

	
	
	
	
}