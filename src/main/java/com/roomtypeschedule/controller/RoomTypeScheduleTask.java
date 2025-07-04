package com.roomtypeschedule.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;
import com.roomtypeschedule.model.RoomTypeScheduleService;

@Component
public class RoomTypeScheduleTask {

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomTypeScheduleService roomTypeScheduleService;

    // 每天凌晨12點執行
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoUpdateRoomTypeSchedules() {
        List<RoomTypeVO> roomTypes = roomTypeService.getAllAvailableRoomTypes();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);

        for (RoomTypeVO roomType : roomTypes) {
            roomTypeScheduleService.initializeOrUpdateRoomTypeSchedule(roomType, startDate, endDate);
        }

        System.out.println("✅ 自動批次更新房型排程完成: " + LocalDate.now());
    }
}
