package com.resto.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.member.model.MemberVO;
import com.resto.utils.RestoOrderSource;
import com.resto.utils.RestoOrderSourceConverter;
import com.resto.utils.RestoOrderStatus;
import com.resto.utils.RestoOrderStatusConverter;
import com.roomOrder.model.RoomOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "resto_order")
public class RestoOrderVO{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resto_order_id", updatable = false)
	private Integer restoOrderId;
	
	// 外表關聯
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private MemberVO memberVO;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROOM_ORDER_ID")
	private RoomOrder roomOrder;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@NotNull
	@JoinColumn(name="resto_id")
	private RestoVO restoVO;
	
	// 預約需求
	@NotNull
	@Column(name = "regi_date")
	private LocalDate regiDate;
	
	@ManyToOne
	@NotNull
	@JoinColumn(name="regi_timeslot_id")
	private TimeslotVO timeslotVO;
	
	@NotNull
    @Min(value = 1)
	@Column(name = "regi_seats")
	private Integer regiSeats;
	
    @Min(value = 0)
	@Column(name = "high_chairs")
	private Integer highChairs;
	
	@Size(max = 500, message = "備註敘述請勿超過500字")
	@Column(name = "regi_req")
	private String regiReq;
	
	// 歷史訂單快照
	@NotBlank
	@Column(name = "snapshot_resto_name", length = 40)
	private String snapshotRestoName;

	@Column(name = "snapshot_resto_name_en", length = 40)
	private String snapshotRestoNameEn;

	@NotBlank
	@Column(name = "snapshot_period_name", length = 10)
	private String snapshotPeriodName;

	@NotBlank
	@Column(name = "snapshot_timeslot_name", length = 5)
	private String snapshotTimeslotName;
	
	// 訂單客戶資料
	@NotBlank
	@Size(max = 30, message = "訂位者姓名請勿超過30字")
	@Column(name = "order_guest_name")
	private String orderGuestName;
	
	@NotBlank
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式錯誤")
	@Column(name = "order_guest_phone")
	private String orderGuestPhone;
	
	@NotBlank
	@Email
	@Size(max = 200, message = "信箱過長，請換一組")
	@Column(name = "order_guest_email")
	private String orderGuestEmail;
	
	@NotNull
	@CreationTimestamp
	@Column(name = "order_time", updatable = false)
	private LocalDateTime orderTime;
	
	
	// 管理+狀態與時效欄位
	@Size(max = 500, message = "備註敘述請勿超過500字")
	@Column(name = "admin_note")
	private String adminNote;
	
	@NotNull
	@Convert(converter = RestoOrderSourceConverter.class)
	@Column(name = "order_source")
	private RestoOrderSource orderSource;
	
	@NotNull
	@Convert(converter = RestoOrderStatusConverter.class)
	@Column(name = "order_status")
	private RestoOrderStatus orderStatus = RestoOrderStatus.CREATED;
	
	@NotNull
	@Column(name = "reserve_expire_time")
	private LocalDateTime reserveExpireTime;

	
	
	
	public RestoOrderVO() {

	}

	
	
	// Hibernate在儲存或更新前自動填入快照欄位(EntityManager.persist/merge()前觸發)
	@PrePersist
	@PreUpdate
	private void fillSnapshot() {
	    if (timeslotVO != null) {
	        snapshotTimeslotName = timeslotVO.getTimeslotName();
	        snapshotPeriodName   = timeslotVO.getPeriodVO().getPeriodName();
	    }
	    if (restoVO != null) {
	        snapshotRestoName    = restoVO.getRestoName();
	        snapshotRestoNameEn  = restoVO.getRestoNameEn();
	    }
	}
	



	public Integer getRestoOrderId() {
		return restoOrderId;
	}




	public void setRestoOrderId(Integer restoOrderId) {
		this.restoOrderId = restoOrderId;
	}




	public MemberVO getMemberVO() {
		return memberVO;
	}




	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}




	public RoomOrder getRoomOrder() {
		return roomOrder;
	}




	public void setRoomOrder(RoomOrder roomOrder) {
		this.roomOrder = roomOrder;
	}




	public RestoVO getRestoVO() {
		return restoVO;
	}




	public void setRestoVO(RestoVO restoVO) {
		this.restoVO = restoVO;
	}




	public LocalDate getRegiDate() {
		return regiDate;
	}




	public void setRegiDate(LocalDate regiDate) {
		this.regiDate = regiDate;
	}




	public TimeslotVO getTimeslotVO() {
		return timeslotVO;
	}




	public void setTimeslotVO(TimeslotVO timeslotVO) {
		this.timeslotVO = timeslotVO;
	}




	public Integer getRegiSeats() {
		return regiSeats;
	}




	public void setRegiSeats(Integer regiSeats) {
		this.regiSeats = regiSeats;
	}




	public Integer getHighChairs() {
		return highChairs;
	}




	public void setHighChairs(Integer highChairs) {
		this.highChairs = highChairs;
	}




	public String getRegiReq() {
		return regiReq;
	}




	public void setRegiReq(String regiReq) {
		this.regiReq = regiReq;
	}




	public String getSnapshotRestoName() {
		return snapshotRestoName;
	}




	public void setSnapshotRestoName(String snapshotRestoName) {
		this.snapshotRestoName = snapshotRestoName;
	}




	public String getSnapshotRestoNameEn() {
		return snapshotRestoNameEn;
	}




	public void setSnapshotRestoNameEn(String snapshotRestoNameEn) {
		this.snapshotRestoNameEn = snapshotRestoNameEn;
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




	public String getOrderGuestName() {
		return orderGuestName;
	}




	public void setOrderGuestName(String orderGuestName) {
		this.orderGuestName = orderGuestName;
	}




	public String getOrderGuestPhone() {
		return orderGuestPhone;
	}




	public void setOrderGuestPhone(String orderGuestPhone) {
		this.orderGuestPhone = orderGuestPhone;
	}




	public String getOrderGuestEmail() {
		return orderGuestEmail;
	}




	public void setOrderGuestEmail(String orderGuestEmail) {
		this.orderGuestEmail = orderGuestEmail;
	}




	public LocalDateTime getOrderTime() {
		return orderTime;
	}




	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}




	public String getAdminNote() {
		return adminNote;
	}




	public void setAdminNote(String adminNote) {
		this.adminNote = adminNote;
	}




	public RestoOrderSource getOrderSource() {
		return orderSource;
	}




	public void setOrderSource(RestoOrderSource orderSource) {
		this.orderSource = orderSource;
	}




	public RestoOrderStatus getOrderStatus() {
		return orderStatus;
	}




	public void setOrderStatus(RestoOrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}




	public LocalDateTime getReserveExpireTime() {
		return reserveExpireTime;
	}




	public void setReserveExpireTime(LocalDateTime reserveExpireTime) {
		this.reserveExpireTime = reserveExpireTime;
	}




	@Override
	public int hashCode() {
		return Objects.hash(restoOrderId);
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestoOrderVO other = (RestoOrderVO) obj;
		return Objects.equals(restoOrderId, other.restoOrderId);
	}




	@Override
	public String toString() {
		return "RestoOrderVO [restoOrderId=" + restoOrderId + ", memberVO=" + memberVO + ", roomOrder=" + roomOrder
				+ ", restoVO=" + restoVO + ", regiDate=" + regiDate + ", timeslotVO=" + timeslotVO + ", regiSeats="
				+ regiSeats + ", highChairs=" + highChairs + ", regiReq=" + regiReq + ", snapshotRestoName="
				+ snapshotRestoName + ", snapshotRestoNameEn=" + snapshotRestoNameEn + ", snapshotPeriodName="
				+ snapshotPeriodName + ", snapshotTimeslotName=" + snapshotTimeslotName + ", orderGuestName="
				+ orderGuestName + ", orderGuestPhone=" + orderGuestPhone + ", orderGuestEmail=" + orderGuestEmail
				+ ", orderTime=" + orderTime + ", adminNote=" + adminNote + ", orderSource=" + orderSource
				+ ", orderStatus=" + orderStatus + ", reserveExpireTime=" + reserveExpireTime + "]";
	}
	

	
	
	
	
}