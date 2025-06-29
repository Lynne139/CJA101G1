package com.room.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roomtype.model.RoomTypeVO;

public interface RoomRepository extends JpaRepository<RoomVO, Integer> {
	boolean existsByRoomId(Integer roomId);

	int countByRoomTypeVOAndRoomSaleStatus(RoomTypeVO roomTypeVO, Byte status);
	
//	@Query("SELECT COUNT(r) FROM RoomVO r WHERE r.roomTypeVO.roomTypeId = :roomTypeId AND r.roomSaleStatus = 1")
//    int countByRoomTypeAndOnSale(@Param("roomTypeId") Integer roomTypeId);
}
