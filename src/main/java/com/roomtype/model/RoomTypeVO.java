package com.roomtype.model;

import java.util.Set;

import com.room.model.RoomVO;
import com.room.model.RoomVO.Save;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "room_type")
public class RoomTypeVO implements java.io.Serializable {
	private static final long serialVersionUID = 11L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_type_id", updatable = false)
	private Integer roomTypeId;

	@Column(name = "room_type_name")
	@NotBlank(message = "房型名稱: 請勿空白", groups = Save.class)
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,50}$", message = "房型名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到50之間", groups = Save.class)
	private String roomTypeName;

	@Column(name = "room_type_amount")
	@NotNull(message = "房型數量: 請勿空白", groups = Save.class)
	@DecimalMin(value = "1", message = "房型數量: 不能小於{value}", groups = Save.class)
	@DecimalMax(value = "1000", message = "房型數量: 不能超過{value}", groups = Save.class)
	private Integer roomTypeAmount;

	@Column(name = "room_type_content")
	@Size(max = 1000, message = "房型介紹:請勿超過1000字", groups = Save.class)
	@NotBlank(message = "房型介紹: 請勿空白", groups = Save.class)
	private String roomTypeContent;

	@Column(name = "room_sale_status")
	private Byte roomSaleStatus;

	@Column(name = "room_type_pic", columnDefinition = "mediumblob", nullable = true)
	private byte[] roomTypePic;

	@Column(name = "room_type_price")
	@NotNull(message = "房型價格: 請勿空白", groups = Save.class)
	@DecimalMin(value = "1000", message = "房型價格: 不能小於{value}", groups = Save.class)
	@DecimalMax(value = "1000000", message = "房型價格: 不能超過{value}", groups = Save.class)
	private Integer roomTypePrice;

	@OneToMany(mappedBy = "roomTypeVO", cascade = CascadeType.ALL)
	private Set<RoomVO> roomVOs;

	public Integer getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(Integer roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	public Integer getRoomTypeAmount() {
		return roomTypeAmount;
	}

	public void setRoomTypeAmount(Integer roomTypeAmount) {
		this.roomTypeAmount = roomTypeAmount;
	}

	public String getRoomTypeContent() {
		return roomTypeContent;
	}

	public void setRoomTypeContent(String roomTypeContent) {
		this.roomTypeContent = roomTypeContent;
	}

	public Byte getRoomSaleStatus() {
		return roomSaleStatus;
	}

	public void setRoomSaleStatus(Byte roomSaleStatus) {
		this.roomSaleStatus = roomSaleStatus;
	}

	public byte[] getRoomTypePic() {
		return roomTypePic;
	}

	public void setRoomTypePic(byte[] roomTypePic) {
		this.roomTypePic = roomTypePic;
	}

	public Integer getRoomTypePrice() {
		return roomTypePrice;
	}

	public void setRoomTypePrice(Integer roomTypePrice) {
		this.roomTypePrice = roomTypePrice;
	}

	public Set<RoomVO> getRoomVOs() {
		return roomVOs;
	}

	public void setRoomVOs(Set<RoomVO> roomVOs) {
		this.roomVOs = roomVOs;
	}


	public RoomTypeVO() {
	}
	public interface Save {}    // 只是個標記介面，驗證群組（Validation Groups）
}
