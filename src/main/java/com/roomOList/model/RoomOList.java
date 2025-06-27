package com.roomOList.model;

import com.room.model.RoomVO;
import com.roomOrder.model.RoomOrder;
import com.roomtype.model.RoomTypeVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="room_order_list")
public class RoomOList {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ROOM_ORDER_LIST_ID")
    private Integer roomOrderListId;

    @ManyToOne
    @NotNull(message = "房型編號不可為空")
    @JoinColumn(name="ROOM_TYPE_ID",referencedColumnName = "ROOM_TYPE_ID")
    private RoomTypeVO roomType;

    @ManyToOne
    @JoinColumn(name="ROOM_ID",referencedColumnName = "ROOM_ID")
    private RoomVO room;

    @ManyToOne
    @JoinColumn(name="ROOM_ORDER_ID",referencedColumnName = "ROOM_ORDER_ID")
    private RoomOrder roomOrder;

    @NotNull(message = "請填入入住人數")
    @Min(value = 1, message = "至少為 1")
    @Column(name="NUMBER_OF_PEOPLE")
    private Integer numberOfPeople;

    @Size(max = 50, message = "特殊需求限50字")
    @Column(name="SPECIAL_REQ")
    private String specialReq;

    @NotNull(message = "房間價格不可為空")
    @Column(name="ROOM_PRICE")
    private Integer roomPrice;

    @NotNull(message = "房間數量不可為空")
    @Min(value = 1, message = "至少為 1")
    @Column(name="ROOM_AMOUNT")
    private Integer roomAmount;

    @Column(name="ROOM_GUEST_NAME")
    private String roomGuestName;

    @Column(name="CREATE_DATE",insertable = false)
    private String createDate;

    @Column(name="LIST_STATUS")
    private String listStatus;

    @Transient
    private Integer roomTypeId;

    public Integer getRoomOrderListId() {
        return roomOrderListId;
    }

    public void setRoomOrderListId(Integer roomOrderListId) {
        this.roomOrderListId = roomOrderListId;
    }

    public RoomTypeVO getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeVO roomType) {
        this.roomType = roomType;
    }

    public RoomVO getRoom() {
        return room;
    }

    public void setRoom(RoomVO room) {
        this.room = room;
    }

    public RoomOrder getRoomOrder() {
        return roomOrder;
    }

    public void setRoomOrder(RoomOrder roomOrder) {
        this.roomOrder = roomOrder;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getSpecialReq() {
        return specialReq;
    }

    public void setSpecialReq(String specialReq) {
        this.specialReq = specialReq;
    }

    public Integer getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(Integer roomPrice) {
        this.roomPrice = roomPrice;
    }

    public Integer getRoomAmount() {
        return roomAmount;
    }

    public void setRoomAmount(Integer roomAmount) {
        this.roomAmount = roomAmount;
    }

    public String getRoomGuestName() {
        return roomGuestName;
    }

    public void setRoomGuestName(String roomGuestName) {
        this.roomGuestName = roomGuestName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getListStatus() {
        return listStatus;
    }

    public void setListStatus(String listStatus) {
        this.listStatus = listStatus;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}
