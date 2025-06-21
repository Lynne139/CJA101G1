package com.roomOrder.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roomOrder.model.RoomOrder;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Long> {
}

