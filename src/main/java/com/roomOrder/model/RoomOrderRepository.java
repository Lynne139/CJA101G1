package com.roomOrder.model;

import com.member.model.MemberVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Integer> {
    // 根據房間訂單 ID 查詢
    RoomOrder findByRoomOrderId(Integer roomOrderId);

    // 根據訂單狀態查詢
    List<RoomOrder> getByOrderStatus(Integer orderStatus);

    // 根據訂單日期範圍查詢
    List<RoomOrder> findByOrderDateBetween(String startDate, String endDate);

    // 用 member 物件查
    RoomOrder findByMember(MemberVO member);

    // 或用 member 的 memberId 查
    List<RoomOrder> findByMember_MemberId(Integer memberId);
}

