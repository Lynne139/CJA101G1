package com.roomtypeschedule.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.room.model.RoomRepository;
import com.room.utils.RoomTypeScheduleCriteriaHelper;
import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service("roomTypeScheduleService")
public class RoomTypeScheduleService {
	@PersistenceContext
	private EntityManager em;

	private final RoomTypeScheduleRepository roomTypeScheduleRepository;
	private final RoomRepository roomRepository;

	public RoomTypeScheduleService(RoomTypeScheduleRepository roomTypeScheduleRepository, RoomRepository roomRepository) {
		this.roomTypeScheduleRepository = roomTypeScheduleRepository;
		this.roomRepository = roomRepository;
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

//    public List<RoomTypeScheduleVO> getMonthlySchedules(Integer roomTypeId, YearMonth month) {
//        Date start = Date.valueOf(month.atDay(1));
//        Date end = Date.valueOf(month.atEndOfMonth());
//        return roomTypeScheduleRepository.findMonthlySchedules(roomTypeId, start, end);
//    }
	
	// 複合查詢（Criteria 結構）
    public List<RoomTypeScheduleVO> compositeQuery(Map<String, String[]> map) {
        return RoomTypeScheduleCriteriaHelper.getRoomTypeScheduleCriteria(map, em);
    }
    
    /**
     * 只在批次建立時用 RoomTypeSchedule
     * @param roomType 房型
     * @param startDate 起始日期
     * @param endDate 結束日期
     */
    @Transactional
    public void initializeOrUpdateRoomTypeSchedule(RoomTypeVO roomType, LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            RoomTypeScheduleVO schedule = roomTypeScheduleRepository
                .findByRoomTypeVOAndRoomOrderDate(roomType, sqlDate)
                .orElseGet(() -> {
                    RoomTypeScheduleVO newSchedule = new RoomTypeScheduleVO();
                    newSchedule.setRoomTypeVO(roomType);
                    newSchedule.setRoomOrderDate(sqlDate);
                    newSchedule.setRoomRSVBooked(0);
                    return newSchedule;
                });

            // 如果是未來或今天，更新可販售總數
            if (!date.isBefore(LocalDate.now())) {
                int availableRooms = roomRepository.countByRoomTypeVOAndRoomSaleStatus(roomType, (byte) 1);
                schedule.setRoomAmount(availableRooms);
            }

            roomTypeScheduleRepository.save(schedule);
        }
    }

    /**
     * 當訂單成立時，更新多天的已預定數量
     */    
//	@Transactional
//	public void reserve(RoomOList roomOList) {
//		RoomTypeVO roomType = roomOList.getRoomType();
//		LocalDate startDate = roomOList.getRoomOrder().getStartDate().toLocalDate();
//		LocalDate endDate = roomOList.getRoomOrder().getEndDate().toLocalDate();
//		int bookedAmount = roomOList.getRoomAmount();
//
//		List<RoomTypeScheduleVO> schedules = roomTypeScheduleRepository.findByRoomTypeVOAndRoomOrderDateBetween(
//				roomType, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
//
//		// 先檢查所有天庫存
//		for (RoomTypeScheduleVO schedule : schedules) {
//			int remaining = schedule.getRoomAmount() - schedule.getRoomRSVBooked();
//			if (remaining < bookedAmount) {
//				throw new IllegalArgumentException(
//						"庫存不足！在 " + schedule.getRoomOrderDate() + " 只剩 " + remaining + " 間房");
//			}
//		}
//
//		// 再逐天寫入
//		for (RoomTypeScheduleVO schedule : schedules) {
//			schedule.setRoomRSVBooked(schedule.getRoomRSVBooked() + bookedAmount);
//			roomTypeScheduleRepository.save(schedule);
//		}
//	}
    
//    @Transactional
//    public void cancelReservation(RoomOList roomOList) {
//        RoomTypeVO roomType = roomOList.getRoomType();
//        LocalDate startDate = roomOList.getRoomOrder().getStartDate().toLocalDate();
//        LocalDate endDate = roomOList.getRoomOrder().getEndDate().toLocalDate();
//        int quantity = roomOList.getRoomAmount();
//
//        List<RoomTypeScheduleVO> schedules = roomTypeScheduleRepository.findByRoomTypeVOAndRoomOrderDateBetween(
//            roomType,
//            java.sql.Date.valueOf(startDate),
//            java.sql.Date.valueOf(endDate)
//        );
//
//        for (RoomTypeScheduleVO schedule : schedules) {
//            int updatedBooked = schedule.getRoomRSVBooked() - quantity;
//            if (updatedBooked <= 0) {
//                schedule.setRoomRSVBooked(0);
//            } else {
//                schedule.setRoomRSVBooked(updatedBooked);
//            }
//            roomTypeScheduleRepository.save(schedule);
//        }
//    }
    
//    @Transactional
//    public void updateReservation(RoomOList oldRoomOList, RoomOList newRoomOList) {
//        RoomTypeVO roomType = oldRoomOList.getRoomType();
//        LocalDate startDate = oldRoomOList.getRoomOrder().getStartDate().toLocalDate();
//        LocalDate endDate = oldRoomOList.getRoomOrder().getEndDate().toLocalDate();
//
//        int oldQty = oldRoomOList.getRoomAmount();
//        int newQty = newRoomOList.getRoomAmount();
//        int diff = newQty - oldQty;
//
//        if (diff > 0) {
//            // 要多訂房 ➜ 檢查並預約
//            List<RoomTypeScheduleVO> schedules = roomTypeScheduleRepository
//                .findByRoomTypeVOAndRoomOrderDateBetween(
//                    roomType,
//                    java.sql.Date.valueOf(startDate),
//                    java.sql.Date.valueOf(endDate)
//                );
//
//            for (RoomTypeScheduleVO schedule : schedules) {
//                int remaining = schedule.getRoomAmount() - schedule.getRoomRSVBooked();
//                if (remaining < diff) {
//                    throw new IllegalArgumentException(
//                        "庫存不足！在 " + schedule.getRoomOrderDate() +
//                        " 只剩 " + remaining + " 間房"
//                    );
//                }
//            }
//
//            for (RoomTypeScheduleVO schedule : schedules) {
//                schedule.setRoomRSVBooked(schedule.getRoomRSVBooked() + diff);
//                roomTypeScheduleRepository.save(schedule);
//            }
//
//        } else if (diff < 0) {
//            // 減少預約 ➜ 釋放庫存
//            List<RoomTypeScheduleVO> schedules = roomTypeScheduleRepository
//                .findByRoomTypeVOAndRoomOrderDateBetween(
//                    roomType,
//                    java.sql.Date.valueOf(startDate),
//                    java.sql.Date.valueOf(endDate)
//                );
//
//            for (RoomTypeScheduleVO schedule : schedules) {
//                int newBooked = schedule.getRoomRSVBooked() + diff; // diff 是負數
//                schedule.setRoomRSVBooked(Math.max(0, newBooked));
//                roomTypeScheduleRepository.save(schedule);
//            }
//
//        } else {
//            // 數量沒變，不動
//        }
//    }


}
