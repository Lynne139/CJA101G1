package com.roomtypeschedule.model;

import java.sql.Date;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.room.utils.RoomTypeScheduleCriteriaHelper;
import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("roomTypeScheduleService")
public class RoomTypeScheduleService {
	@PersistenceContext
	private EntityManager em;

	private final RoomTypeScheduleRepository roomTypeScheduleRepository;

	public RoomTypeScheduleService(RoomTypeScheduleRepository roomTypeScheduleRepository) {
		this.roomTypeScheduleRepository = roomTypeScheduleRepository;
	}
	
	public void addRoomTypeSchedule(RoomTypeScheduleVO roomTypeScheduleVO) {
		roomTypeScheduleRepository.save(roomTypeScheduleVO);
	}
	
	public void updateRoomTypeSchedule(RoomTypeScheduleVO roomTypeScheduleVO) {
		roomTypeScheduleRepository.save(roomTypeScheduleVO);
	}
	
	public RoomTypeScheduleVO getOneRoomTypeSchedule(Integer roomTypeScheduleId) {
		Optional<RoomTypeScheduleVO> optional = roomTypeScheduleRepository.findById(roomTypeScheduleId);
		return optional.orElse(null);
	}
	
	public List<RoomTypeScheduleVO> getAll() {
		return roomTypeScheduleRepository.findAll();
	}
	
	public List<RoomTypeScheduleVO> getByOrderDate(Date roomOrderDate) {
		return roomTypeScheduleRepository.findByRoomOrderDate(roomOrderDate);
	}
	
	public Optional<RoomTypeScheduleVO> getByRoomTypeVOAndRoomOrderDate(RoomTypeVO roomTypeVO, Date roomOrderDate) {
        return roomTypeScheduleRepository.findByRoomTypeVOAndRoomOrderDate(roomTypeVO, roomOrderDate);
    }
	
	public Optional<RoomTypeScheduleVO> getByRoomTypeVO_RoomTypeIdAndRoomOrderDate(Integer roomTypeId, Date roomOrderDate) {
		return roomTypeScheduleRepository.findByRoomTypeVO_RoomTypeIdAndRoomOrderDate(roomTypeId, roomOrderDate);
	}

    public List<RoomTypeScheduleVO> getMonthlySchedules(Integer roomTypeId, YearMonth month) {
        Date start = Date.valueOf(month.atDay(1));
        Date end = Date.valueOf(month.atEndOfMonth());
        return roomTypeScheduleRepository.findMonthlySchedules(roomTypeId, start, end);
    }
	
	public void bookRoom(RoomTypeVO roomTypeVO, Date roomOrderDate, int quantity) {
        Optional<RoomTypeScheduleVO> optional = roomTypeScheduleRepository.findByRoomTypeVOAndRoomOrderDate(roomTypeVO, roomOrderDate);

        if (optional.isPresent()) {
            // 如果已存在，就更新數量
            RoomTypeScheduleVO existing = optional.get();
            existing.setRoomRSVBooked(existing.getRoomRSVBooked() + quantity);
            roomTypeScheduleRepository.save(existing);
        } else {
            // 如果不存在，就新增一筆
            RoomTypeScheduleVO newSchedule = new RoomTypeScheduleVO();
            newSchedule.setRoomTypeVO(roomTypeVO);
            newSchedule.setRoomOrderDate(roomOrderDate);
            newSchedule.setRoomRSVBooked(quantity);
            roomTypeScheduleRepository.save(newSchedule);
        }
    }
	
	public void cancelBooking(RoomTypeVO roomTypeVO, Date roomOrderDate, int quantity) {
	    Optional<RoomTypeScheduleVO> optional = roomTypeScheduleRepository.findByRoomTypeVOAndRoomOrderDate(roomTypeVO, roomOrderDate);

	    if (optional.isPresent()) {
	        RoomTypeScheduleVO schedule = optional.get();
	        int updatedBooked = schedule.getRoomRSVBooked() - quantity;

	        if (updatedBooked <= 0) {
	            // 可選擇：刪除資料 or 設為 0
	        	//repository.delete(schedule); // 選項 1：刪除資料
	            schedule.setRoomRSVBooked(0); roomTypeScheduleRepository.save(schedule); // 選項 2：保留但歸 0
	        } else {
	            schedule.setRoomRSVBooked(updatedBooked);
	            roomTypeScheduleRepository.save(schedule);
	        }
	    } else {
	        // 正常情況下不應該發生
	        throw new RuntimeException("取消訂單失敗：找不到對應的 RoomTypeSchedule");
	    }
	}
	
	// 複合查詢（Criteria 結構）
    public List<RoomTypeScheduleVO> compositeQuery(Map<String, String[]> map) {
        return RoomTypeScheduleCriteriaHelper.getRoomTypeScheduleCriteria(map, em);
    }
}
