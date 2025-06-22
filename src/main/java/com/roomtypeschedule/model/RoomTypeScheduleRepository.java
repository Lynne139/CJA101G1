package com.roomtypeschedule.model;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roomtype.model.RoomTypeVO;

public interface RoomTypeScheduleRepository extends JpaRepository<RoomTypeScheduleVO, Integer>{
	
	List<RoomTypeScheduleVO> findByRoomOrderDate(Date roomOrderDate);
	
	// 查某天某房型
	Optional<RoomTypeScheduleVO> findByRoomTypeVOAndRoomOrderDate(RoomTypeVO roomTypeVO, Date roomOrderDate);
	
	Optional<RoomTypeScheduleVO> findByRoomTypeVO_RoomTypeIdAndRoomOrderDate(Integer roomTypeId, Date roomOrderDate);
	
	// 查某房型某月所有資料
	@Query("SELECT s FROM RoomTypeScheduleVO s WHERE s.roomTypeVO.roomTypeId = :roomTypeId AND s.roomOrderDate BETWEEN :start AND :end")
	List<RoomTypeScheduleVO> findMonthlySchedules(
	    @Param("roomTypeId") Integer roomTypeId,
	    @Param("start") Date start,
	    @Param("end") Date end
	);

}
