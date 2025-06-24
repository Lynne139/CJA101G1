package com.roomOrder.model;

import com.resto.model.RestoVO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.roomOrder.model.RoomOrder;

import java.util.List;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Long> {


}

