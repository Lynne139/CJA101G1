package com.room.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roomtype.model.RoomTypeRepository;
import com.roomtype.model.RoomTypeVO;

@Service("roomService")
public class RoomService {
	@Autowired
	RoomRepository repository;

	@Autowired
    private SessionFactory sessionFactory;
	
	public void addRoom(RoomVO roomVO) {
		repository.save(roomVO);
	}
	
	public void updateRoom(RoomVO roomVO) {
		repository.save(roomVO);
	}
	
	public RoomVO getOneRoom(Integer roomId) {
		Optional<RoomVO> optional = repository.findById(roomId);
		return optional.orElse(null);
	}
	
	public List<RoomVO> getAll() {
		return repository.findAll();
	}
}
