package com.resto.model;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.resto.entity.RestoOrderVO;
import com.resto.utils.RestoOrderStatus;

public interface RestoOrderRepository extends JpaRepository<RestoOrderVO, Integer>  {

		// 每日該餐廳總訂位
		@Query("""
		    SELECT ro.regiDate, SUM(ro.regiSeats)
		    FROM RestoOrderVO ro
		    WHERE ro.restoVO.restoId = :restoId 
		      AND ro.orderStatus <> com.resto.utils.RestoOrderStatus.CANCELED
		    GROUP BY ro.regiDate
		""")
		List<Object[]> findBookedSeatsPerDate(@Param("restoId") Integer restoId);

	
	
	
	
	
}