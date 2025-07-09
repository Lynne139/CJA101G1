package com.resto.model;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resto.dto.RestoOrderSummaryDTO;
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
	
	// 抓今天特定狀態的訂單(排程器定時切狀態用)
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

	

	// 統計特定 restoId 的所有狀態訂單 
	@Query("""
			SELECT new com.resto.dto.RestoOrderSummaryDTO(
			    COUNT(ro),
			    SUM( CASE WHEN ro.orderStatus = com.resto.utils.RestoOrderStatus.DONE       THEN 1 ELSE 0 END ),
			    SUM( CASE WHEN ro.orderStatus = com.resto.utils.RestoOrderStatus.NOSHOW     THEN 1 ELSE 0 END ),
			    SUM( CASE WHEN ro.orderStatus IN (
			                  com.resto.utils.RestoOrderStatus.CREATED,
			                  com.resto.utils.RestoOrderStatus.WITHHOLD )                   THEN 1 ELSE 0 END ),
			    SUM( CASE WHEN ro.orderStatus = com.resto.utils.RestoOrderStatus.CANCELED   THEN 1 ELSE 0 END ),
			    COALESCE(SUM(ro.regiSeats), 0)
			)
			FROM RestoOrderVO ro
			WHERE ro.regiDate = CURRENT_DATE
			  AND (:restoId IS NULL OR ro.restoVO.restoId = :restoId)
			""")
			RestoOrderSummaryDTO findTodaySummary(@Param("restoId") Integer restoId);

	
	// 餐廳總統計
	@Query("""
			SELECT new com.resto.dto.RestoOrderSummaryDTO(
			    ro.restoVO.restoName,
			    COUNT(ro),
			    SUM( CASE WHEN ro.orderStatus = com.resto.utils.RestoOrderStatus.DONE       THEN 1 ELSE 0 END ),
			    SUM( CASE WHEN ro.orderStatus = com.resto.utils.RestoOrderStatus.NOSHOW     THEN 1 ELSE 0 END ),
			    SUM( CASE WHEN ro.orderStatus IN (
			                  com.resto.utils.RestoOrderStatus.CREATED,
			                  com.resto.utils.RestoOrderStatus.WITHHOLD )                   THEN 1 ELSE 0 END ),
			    SUM( CASE WHEN ro.orderStatus = com.resto.utils.RestoOrderStatus.CANCELED   THEN 1 ELSE 0 END ),
			    COALESCE(SUM(ro.regiSeats), 0)
			)
			FROM RestoOrderVO ro
			WHERE ro.regiDate = CURRENT_DATE
			GROUP BY ro.restoVO.restoName
			ORDER BY ro.restoVO.restoName
			""")
			List<RestoOrderSummaryDTO> findAllTodaySummaryPerResto();


	
	
	// 會員訂單
	// 查詢某會員的所有訂單，依日期從新到舊排序
    List<RestoOrderVO> findAllByMemberVO_MemberIdOrderByRegiDateDesc(Integer memberId);




	
}