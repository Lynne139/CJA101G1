package com.room.model;

import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
<<<<<<< Upstream, based on branch 'master' of https://github.com/Lynne139/CJA101G1.git
=======
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
>>>>>>> 3e353de listAllRoomType update

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
	private String roomGuestName;
	
	@Column(name = "room_sale_status")
	private Byte roomSaleStatus;
	
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
	public void setRoomTypeHibernateVO(RoomTypeVO roomTypeVO) {
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
}
