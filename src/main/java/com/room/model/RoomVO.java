package com.room.model;

import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "room")
public class RoomVO {
	@Id
	@Column(name = "room_id", updatable = false)
	@NotNull(message="房間編號: 請勿空白")
	@Digits(integer = 4, fraction = 0, message = "房間編號: 請填數字-請勿超過{integer}位數")
	@DecimalMin(value = "1000", message = "房間編號: 不能小於{value}")
	@DecimalMax(value = "9999", message = "房間編號: 不能超過{value}")
	private Integer roomId;
	
	@ManyToOne
	@JoinColumn(name = "room_type_id", referencedColumnName = "room_type_id")
	private RoomTypeVO roomTypeVO;
	
	@Column(name = "room_guest_name", nullable = true)
	@Pattern(
		    regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-]{2,50}$",
		    message = "房客名稱: 只能是中、英文字母、數字與 -，長度需在 2 到 50 字內"
		)
	private String roomGuestName;
	
	@NotNull(message = "上下架狀態: 請選擇一個狀態")
	@Min(value = 0, message = "")
	@Max(value = 1, message = "")
	@Column(name = "room_sale_status")
	private Byte roomSaleStatus;
	
	@NotNull(message = "房間狀態: 請選擇一個狀態")
	@Min(value = 0, message = "")
	@Max(value = 2, message = "")
	@Column(name = "room_status")
	private Byte roomStatus;
	
	public Integer getRoomId() {
		return roomId;
	}
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}
	public RoomTypeVO getRoomTypeVO() {
		return roomTypeVO;
	}
	public void setRoomTypeVO(RoomTypeVO roomTypeVO) {
		this.roomTypeVO = roomTypeVO;
	}
	public String getRoomGuestName() {
		return roomGuestName;
	}
	public void setRoomGuestName(String roomGuestName) {
		this.roomGuestName = roomGuestName;
	}
	public Byte getRoomSaleStatus() {
		return roomSaleStatus;
	}
	public void setRoomSaleStatus(Byte roomSaleStatus) {
		this.roomSaleStatus = roomSaleStatus;
	}
	public Byte getRoomStatus() {
		return roomStatus;
	}
	public void setRoomStatus(Byte roomStatus) {
		this.roomStatus = roomStatus;
	}
	public RoomVO() {
	}
	
}
