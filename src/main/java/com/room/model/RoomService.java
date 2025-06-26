package com.room.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.room.utils.RoomCriteriaHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("roomService")
public class RoomService {
	@PersistenceContext
	private EntityManager em;

	private final RoomRepository roomRepository;

	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}
	
	public void addRoom(RoomVO roomVO) {
		 // 檢查主鍵是否已存在
	    if (roomRepository.existsByRoomId(roomVO.getRoomId())) {
	        throw new IllegalArgumentException("房號已存在");
	    }
		roomRepository.save(roomVO);
	}
	
	public void updateRoom(RoomVO roomVO) {
		roomRepository.save(roomVO);
	}
	
	public RoomVO getOneRoom(Integer roomId) {
		Optional<RoomVO> optional = roomRepository.findById(roomId);
		return optional.orElse(null);
	}
	
	public List<RoomVO> getAll() {
		return roomRepository.findAll();
	}
	
	// 複合查詢（Criteria 結構）
    public List<RoomVO> compositeQuery(Map<String, String[]> map) {
        return RoomCriteriaHelper.getRoomCriteria(map, em);
    }
}
