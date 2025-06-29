package com.roomtype.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resto.entity.RestoVO;

public interface RoomTypeRepository extends JpaRepository<RoomTypeVO, Integer>{
	
	Optional<RoomTypeVO> findByRoomTypeName(String roomTypeName);
	
	List<RoomTypeVO> findByRoomSaleStatus(Byte roomSaleStatus);
}
