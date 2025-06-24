package com.roomOrder.model;

import com.member.model.MemberVO;

import jakarta.persistence.*;

@Entity
@Table(name = "room_order")
public class RoomOrder {
    @Id
    @Column(name = "ROOM_ORDER_ID")
    private Integer roomOrderId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "member_id")
    private MemberVO member;

    @Column(name = "ORDER_DATE")
    private String orderDate;

    @Column(name = "ROOM_ORDER_STATUS")
    private Integer orderStatus;

    @Column(name = "ROOM_AMOUNT")
    private Integer roomAmount;

    @Column(name = "CHECK_IN_DATE")
    private String checkInDate;

    @Column(name = "CHECK_OUT_DATE")
    private String checkOutDate;

    @Column(name = "COUPON_CODE")
    private String couponCode;

    @Column(name = "DISCOUNT_AMOUNT")
    private Integer discountAmount;

    @Column(name = "ACTUAL_AMOUNT")
    private Integer actualAmount;

    @Column(name = "PROJECT_ADD_ON")
    private Integer projectAddOn;

    public Integer getRoomOrderId() {
        return roomOrderId;
    }

    public void setRoomOrderId(Integer roomOrderId) {
        this.roomOrderId = roomOrderId;
    }

    public MemberVO getMember() {
        return member;
    }

    public void setMember(MemberVO member) {
        this.member = member;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRoomAmount() {
        return roomAmount;
    }

    public void setRoomAmount(Integer roomAmount) {
        this.roomAmount = roomAmount;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Integer actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Integer getProjectAddOn() {
        return projectAddOn;
    }

    public void setProjectAddOn(Integer projectAddOn) {
        this.projectAddOn = projectAddOn;
    }

}