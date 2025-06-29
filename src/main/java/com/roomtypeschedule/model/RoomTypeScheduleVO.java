package com.roomtypeschedule.model;

import java.sql.Date;

import com.roomtype.model.RoomTypeVO;
import com.roomtype.model.RoomTypeVO.Save;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "room_type_schedule")
public class RoomTypeScheduleVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_type_schedule_id", updatable = false)
	private Integer roomTypeScheduleId;
	
	@ManyToOne
	@JoinColumn(name = "room_type_id", referencedColumnName = "room_type_id")
	private RoomTypeVO roomTypeVO;
	
	@Column(name = "room_amount")
	@NotNull(message="房間數量: 請勿空白", groups = Save.class)
	@DecimalMin(value = "1", message = "房型數量: 不能小於{value}", groups = Save.class)
	@DecimalMax(value = "1000", message = "房型數量: 不能超過{value}", groups = Save.class)
	private Integer roomAmount;
	
	@Column(name = "room_rsv_booked")
	@DecimalMin(value = "1", message = "房型數量: 不能小於{value}", groups = Save.class)
	@DecimalMax(value = "1000", message = "房型數量: 不能超過{value}", groups = Save.class)
	@NotNull(message="已訂房間數量: 請勿空白")
	private Integer roomRSVBooked;
	
	@Column(name = "room_order_date")
	private Date roomOrderDate;
	
	public Integer getRoomTypeScheduleId() {
		return roomTypeScheduleId;
	}
	public void setRoomTypeScheduleId(Integer roomTypeScheduleId) {
		this.roomTypeScheduleId = roomTypeScheduleId;
	}
	public RoomTypeVO getRoomTypeVO() {
		return roomTypeVO;
	}
	public void setRoomTypeVO(RoomTypeVO roomTypeVO) {
		this.roomTypeVO = roomTypeVO;
	}
	public Integer getRoomAmount() {
		return roomAmount;
	}
	public void setRoomAmount(Integer roomAmount) {
		this.roomAmount = roomAmount;
	}
	public Integer getRoomRSVBooked() {
		return roomRSVBooked;
	}
	public void setRoomRSVBooked(Integer roomRSVBooked) {
		this.roomRSVBooked = roomRSVBooked;
	}
	public Date getRoomOrderDate() {
		return roomOrderDate;
	}
	public void setRoomOrderDate(Date roomOrderDate) {
		this.roomOrderDate = roomOrderDate;
	}
	public RoomTypeScheduleVO() {
//		this.roomRSVBooked = 0;
	}
}
