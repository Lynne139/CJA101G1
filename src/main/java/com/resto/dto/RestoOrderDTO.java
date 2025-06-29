package com.resto.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


import com.resto.utils.RestoOrderSource;
import com.resto.utils.RestoOrderStatus;


public class RestoOrderDTO {
	
	private Integer restoOrderId;
	private String snapshotRestoName;
	private RestoOrderSource orderSource;
	private Integer memberId;
	private Integer roomOrderId;
	private String orderGuestName;
	private String orderGuestEmail;
	private String orderGuestPhone;
	private LocalDate regiDate;
	private String snapshotPeriodName;
	private String snapshotTimeslotName;
	private Integer regiSeats;
	private LocalDateTime orderTime;
	private RestoOrderStatus orderStatus = RestoOrderStatus.CREATED;
	
	
	public RestoOrderDTO() {}


	


	public RestoOrderDTO(Integer restoOrderId, String snapshotRestoName, RestoOrderSource orderSource, Integer memberId,
			Integer roomOrderId, String orderGuestName, String orderGuestEmail, String orderGuestPhone,
			LocalDate regiDate, String snapshotPeriodName, String snapshotTimeslotName, Integer regiSeats,
			LocalDateTime orderTime, RestoOrderStatus orderStatus) {
		super();
		this.restoOrderId = restoOrderId;
		this.snapshotRestoName = snapshotRestoName;
		this.orderSource = orderSource;
		this.memberId = memberId;
		this.roomOrderId = roomOrderId;
		this.orderGuestName = orderGuestName;
		this.orderGuestEmail = orderGuestEmail;
		this.orderGuestPhone = orderGuestPhone;
		this.regiDate = regiDate;
		this.snapshotPeriodName = snapshotPeriodName;
		this.snapshotTimeslotName = snapshotTimeslotName;
		this.regiSeats = regiSeats;
		this.orderTime = orderTime;
		this.orderStatus = orderStatus;
	}





	public Integer getRestoOrderId() {
		return restoOrderId;
	}


	public void setRestoOrderId(Integer restoOrderId) {
		this.restoOrderId = restoOrderId;
	}


	public String getSnapshotRestoName() {
		return snapshotRestoName;
	}


	public void setSnapshotRestoName(String snapshotRestoName) {
		this.snapshotRestoName = snapshotRestoName;
	}


	public RestoOrderSource getOrderSource() {
		return orderSource;
	}


	public void setOrderSource(RestoOrderSource orderSource) {
		this.orderSource = orderSource;
	}


	public Integer getMemberId() {
		return memberId;
	}


	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}


	public Integer getRoomOrderId() {
		return roomOrderId;
	}


	public void setRoomOrderId(Integer roomOrderId) {
		this.roomOrderId = roomOrderId;
	}


	public String getOrderGuestName() {
		return orderGuestName;
	}


	public void setOrderGuestName(String orderGuestName) {
		this.orderGuestName = orderGuestName;
	}


	public String getOrderGuestEmail() {
		return orderGuestEmail;
	}


	public void setOrderGuestEmail(String orderGuestEmail) {
		this.orderGuestEmail = orderGuestEmail;
	}


	public String getOrderGuestPhone() {
		return orderGuestPhone;
	}


	public void setOrderGuestPhone(String orderGuestPhone) {
		this.orderGuestPhone = orderGuestPhone;
	}


	public LocalDate getRegiDate() {
		return regiDate;
	}


	public void setRegiDate(LocalDate regiDate) {
		this.regiDate = regiDate;
	}


	public String getSnapshotPeriodName() {
		return snapshotPeriodName;
	}


	public void setSnapshotPeriodName(String snapshotPeriodName) {
		this.snapshotPeriodName = snapshotPeriodName;
	}


	public String getSnapshotTimeslotName() {
		return snapshotTimeslotName;
	}


	public void setSnapshotTimeslotName(String snapshotTimeslotName) {
		this.snapshotTimeslotName = snapshotTimeslotName;
	}


	public Integer getRegiSeats() {
		return regiSeats;
	}


	public void setRegiSeats(Integer regiSeats) {
		this.regiSeats = regiSeats;
	}


	public LocalDateTime getOrderTime() {
		return orderTime;
	}


	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}


	public RestoOrderStatus getOrderStatus() {
		return orderStatus;
	}


	public void setOrderStatus(RestoOrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}


	@Override
	public String toString() {
		return "RestoOrderDTO [restoOrderId=" + restoOrderId + ", restoName=" + snapshotRestoName + ", orderSource="
				+ orderSource + ", memberId=" + memberId + ", roomOrderId=" + roomOrderId + ", orderGuestName="
				+ orderGuestName + ", orderGuestEmail=" + orderGuestEmail + ", orderGuestPhone=" + orderGuestPhone
				+ ", regiDate=" + regiDate + ", periodName=" + snapshotPeriodName + ", timeslotName=" + snapshotTimeslotName
				+ ", regiSeats=" + regiSeats + ", orderTime=" + orderTime + ", orderStatus=" + orderStatus + "]";
	}


	
	
	
	
	
	
	

}
