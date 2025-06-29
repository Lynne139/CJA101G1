package com.resto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.resto.dto.RestoOrderDTO;
import com.resto.entity.RestoOrderVO;

public interface RestoOrderRepository extends JpaRepository<RestoOrderVO, Integer>  {

	@Query("""
		    SELECT new com.resto.dto.RestoOrderDTO(
		        o.restoOrderId,
		        o.snapshotRestoName,
		        o.orderSource,
		        (o.memberVO != null ? o.memberVO.memberId : null),
		        (o.roomOrder != null ? o.roomOrder.roomOrderId : null),
		        o.orderGuestName,
		        o.regiDate,
		        o.snapshotPeriodName,
		        o.snapshotTimeslotName,
		        o.regiSeats,
		        o.orderTime,
		        o.orderStatus)
		    FROM RestoOrderVO o
		    ORDER BY o.orderTime DESC
		""")
		List<RestoOrderDTO> findAllDTO();

	
	
	
	
	
	
	
}
