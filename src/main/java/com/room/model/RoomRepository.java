package com.room.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roomtype.model.RoomTypeVO;

public interface RoomRepository extends JpaRepository<RoomVO, Integer> {
	boolean existsByRoomId(Integer roomId);

	int countByRoomTypeVOAndRoomSaleStatus(RoomTypeVO roomTypeVO, Byte status);
}
