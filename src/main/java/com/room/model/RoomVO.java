package com.room.model;

import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room")
public class RoomVO {
	@Id
	@Column(name = "room_id", updatable = false)
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
