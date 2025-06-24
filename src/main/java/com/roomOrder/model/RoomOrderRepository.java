package com.roomOrder.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Long> {

    // 根據房間訂單 ID 查詢
    RoomOrder findByRoomOrderId(Integer roomOrderId);


    // 根據訂單狀態查詢
    List<RoomOrder> getByOrderStatus(Integer orderStatus);

    // 根據訂單日期範圍查詢
    List<RoomOrder> findByOrderDateBetween(String startDate, String endDate);
}

