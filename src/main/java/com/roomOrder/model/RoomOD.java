package com.roomOrder.model;

import jakarta.persistence.*;

@Entity
@Table(name="room_order_list")
public class RoomOD {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ROOM_ORDER_LIST_ID",updatable = false)
    private int roomOrderListId;

    private int roomTypeId;

    private int roomId;

    private int roomOrderId;


}
