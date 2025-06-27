package com.roomOrder.model;

import java.util.ArrayList;
import java.util.List;

import com.coupon.entity.Coupon;
import com.employee.entity.Employee;
import com.member.model.MemberVO;
import com.roomOList.model.RoomOList;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "room_order")
public class RoomOrder {
    @Id
    @Column(name = "ROOM_ORDER_ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer roomOrderId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "member_id")
    private MemberVO member;

    @Column(name = "ORDER_DATE", insertable = false, updatable = false)
    private String orderDate;


    @Column(name = "ROOM_ORDER_STATUS")
    private Integer orderStatus;

    @NotNull(message = "房間數量不可為0")
    @Column(name = "ROOM_AMOUNT")
    private Integer roomAmount;

    @Column(name="ROOM_COUNT")
    private Integer roomCount;

    @NotNull(message = "入住日期不可為空")
    @Column(name = "CHECK_IN_DATE")
    private String checkInDate;

    @NotNull(message = "退房日期不可為空")
    @Column(name = "CHECK_OUT_DATE")
    private String checkOutDate;

    @ManyToOne
    @JoinColumn(name = "COUPON_CODE",referencedColumnName = "coupon_code")
    private Coupon coupon;

    @Column(name = "DISCOUNT_AMOUNT")
    private Integer discountAmount;

    @NotNull(message = "實際支付金額不可為空")
    @Column(name = "ACTUAL_AMOUNT")
    private Integer actualAmount;

    @Column(name = "PROJECT_ADD_ON")
    private Integer projectAddOn;

    @Column(name = "UPDATE_DATE",insertable = false)
    private String updateDate;

    @ManyToOne
    @JoinColumn(name = "UPDATE_EMP", referencedColumnName = "Employee_id")
    private Employee employee;

    @Column(name="PAY_METHOD")
    private String payMethod;

    @Column(name="PAY_STATUS")
    private String payStatus;

    @Transient
    private List<RoomOList> orderDetails;

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

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(Integer roomCount) {
        this.roomCount = roomCount;
    }

    public List<RoomOList> getOrderDetails() {
        // 保證永遠不會回傳 null
        return orderDetails == null ? new ArrayList<>() : orderDetails;
    }
    public void setOrderDetails(List<RoomOList> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    


}