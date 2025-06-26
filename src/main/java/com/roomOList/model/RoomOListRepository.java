package com.roomOList.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomOListRepository extends JpaRepository<RoomOList, Integer> {
    // 根據訂單明細ID查詢
    RoomOList findByRoomOrderListId(Integer RoomOrderListId);

    // 新增訂單明細
    RoomOList save(RoomOList roomOList);

    // 刪除訂單明細
    void deleteByRoomOrderListId(Integer RoomOrderListId);

    // 查詢所有訂單明細
    List<RoomOList> findAll();

    // 根據訂單編號ID查詢所有訂單明細
    List<RoomOList> findByRoomOrder_RoomOrderId(Integer roomOrderId);


}
