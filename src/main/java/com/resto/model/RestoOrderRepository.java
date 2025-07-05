package com.resto.model;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resto.dto.RestoOrderStatsDTO;
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

	
	
	// 只抓「今天」＋「指定餐廳（可選）」並排序
	@Query("""
		    FROM RestoOrderVO ro
		    WHERE ro.regiDate = CURRENT_DATE
		      AND (:restoId IS NULL OR ro.restoVO.restoId = :restoId)
		    ORDER BY ro.regiDate,
		             ro.snapshotTimeslotName,
		             ro.restoVO.restoId
		""")
		List<RestoOrderVO> findTodayOrders(@Param("restoId") Integer restoId);

	
	
	// 針對特定 restoId 和 regiDate = today 的訂單做分狀態統計
	@Query("""
		    SELECT new com.resto.dto.RestoOrderStatsDTO(COUNT(ro), COALESCE(SUM(ro.regiSeats), 0))
		    FROM RestoOrderVO ro
		    WHERE ro.regiDate = CURRENT_DATE
		      AND ro.restoVO.restoId = :restoId
		      AND ro.orderStatus = :status
		""")
		RestoOrderStatsDTO findTodayStatsByRestoAndStatus(@Param("restoId") Integer restoId,
		                                                   @Param("status") RestoOrderStatus status);

	// 統計特定 restoId 的所有狀態訂單 
	@Query("""
		    SELECT new com.resto.dto.RestoOrderStatsDTO(COUNT(ro), COALESCE(SUM(ro.regiSeats), 0))
		    FROM RestoOrderVO ro
		    WHERE ro.regiDate = CURRENT_DATE
		      AND ro.restoVO.restoId = :restoId
		""")
		RestoOrderStatsDTO findTodayStatsByResto(@Param("restoId") Integer restoId);
	
	
}