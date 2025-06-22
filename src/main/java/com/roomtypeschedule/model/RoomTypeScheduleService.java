package com.roomtypeschedule.model;

import java.sql.Date;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roomtype.model.RoomTypeRepository;
import com.roomtype.model.RoomTypeVO;

@Service("roomTypeScheduleService")
public class RoomTypeScheduleService {
	@Autowired
	RoomTypeScheduleRepository repository;

	@Autowired
    private SessionFactory sessionFactory;
	
	public void addRoomTypeSchedule(RoomTypeScheduleVO roomTypeScheduleVO) {
		repository.save(roomTypeScheduleVO);
	}
	
	public void updateRoomTypeSchedule(RoomTypeScheduleVO roomTypeScheduleVO) {
		repository.save(roomTypeScheduleVO);
	}
	
	public RoomTypeScheduleVO getOneRoomTypeSchedule(Integer roomTypeScheduleId) {
		Optional<RoomTypeScheduleVO> optional = repository.findById(roomTypeScheduleId);
		return optional.orElse(null);
	}
	
	public List<RoomTypeScheduleVO> getAll() {
		return repository.findAll();
	}
	
	public List<RoomTypeScheduleVO> getByOrderDate(Date roomOrderDate) {
		return repository.findByRoomOrderDate(roomOrderDate);
	}
	
	public Optional<RoomTypeScheduleVO> getByRoomTypeVOAndRoomOrderDate(RoomTypeVO roomTypeVO, Date roomOrderDate) {
        return repository.findByRoomTypeVOAndRoomOrderDate(roomTypeVO, roomOrderDate);
    }
	
	public Optional<RoomTypeScheduleVO> getByRoomTypeVO_RoomTypeIdAndRoomOrderDate(Integer roomTypeId, Date roomOrderDate) {
		return repository.findByRoomTypeVO_RoomTypeIdAndRoomOrderDate(roomTypeId, roomOrderDate);
	}

    public List<RoomTypeScheduleVO> getMonthlySchedules(Integer roomTypeId, YearMonth month) {
        Date start = Date.valueOf(month.atDay(1));
        Date end = Date.valueOf(month.atEndOfMonth());
        return repository.findMonthlySchedules(roomTypeId, start, end);
    }
	
	public void bookRoom(RoomTypeVO roomTypeVO, Date roomOrderDate, int quantity) {
        Optional<RoomTypeScheduleVO> optional = repository.findByRoomTypeVOAndRoomOrderDate(roomTypeVO, roomOrderDate);

        if (optional.isPresent()) {
            // 如果已存在，就更新數量
            RoomTypeScheduleVO existing = optional.get();
            existing.setRoomRSVBooked(existing.getRoomRSVBooked() + quantity);
            repository.save(existing);
        } else {
            // 如果不存在，就新增一筆
            RoomTypeScheduleVO newSchedule = new RoomTypeScheduleVO();
            newSchedule.setRoomTypeVO(roomTypeVO);
            newSchedule.setRoomOrderDate(roomOrderDate);
            newSchedule.setRoomRSVBooked(quantity);
            repository.save(newSchedule);
        }
    }
	
	public void cancelBooking(RoomTypeVO roomTypeVO, Date roomOrderDate, int quantity) {
	    Optional<RoomTypeScheduleVO> optional = repository.findByRoomTypeVOAndRoomOrderDate(roomTypeVO, roomOrderDate);

	    if (optional.isPresent()) {
	        RoomTypeScheduleVO schedule = optional.get();
	        int updatedBooked = schedule.getRoomRSVBooked() - quantity;

	        if (updatedBooked <= 0) {
	            // 可選擇：刪除資料 or 設為 0
	        	//repository.delete(schedule); // 選項 1：刪除資料
	            schedule.setRoomRSVBooked(0); repository.save(schedule); // 選項 2：保留但歸 0
	        } else {
	            schedule.setRoomRSVBooked(updatedBooked);
	            repository.save(schedule);
	        }
	    } else {
	        // 正常情況下不應該發生
	        throw new RuntimeException("取消訂單失敗：找不到對應的 RoomTypeSchedule");
	    }
	}
}
