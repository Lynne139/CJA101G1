package com.resto.model;

import java.util.Objects;

import org.springframework.data.annotation.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "resto")
public class RestoVO implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	@Version
	@Column(name = "version", nullable = false)
	private Integer version = 0;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resto_id", updatable = false)
	private Integer restoId;

	@NotBlank(message = "餐廳名稱不得為空")
	@Size(max = 40, message = "餐廳名稱請勿超過40字")
	@Column(name = "resto_name")
	private String restoName;
	
    @Size(max = 40, message = "英文名稱請勿超過40字")
	@Column(name = "resto_name_en")
	private String restoNameEn;
	
    @NotNull(message = "請填入可容納人數")
    @Min(value = 1, message = "人數至少為 1")
    @Max(value = 1000, message = "人數不得超過1000")
	@Column(name = "resto_seats_total")
	private Integer restoSeatsTotal;
	
    @Size(max = 150, message = "簡介請勿超過150字")
	@Column(name = "resto_info")
	private String restoInfo;
	
    @Size(max = 30, message = "類型請勿超過30字")
	@Column(name = "resto_type")
	private String restoType;
	
	@Column(name = "resto_content", columnDefinition = "longtext")
	private String restoContent;
	
	@Size(max = 16,message = "電話請勿超過16字")
	@Column(name = "resto_phone")
	private String restoPhone;
	
	@Size(max = 30, message = "地點請勿超過30字")
	@Column(name = "resto_loc")
	private String restoLoc;
	
	@Transient
	private byte[] restoImg;
	
	@Column(name = "is_enabled") //前台上下架渲染
	private Boolean isEnabled = true;
	
	@Column(name = "is_deleted") //軟刪除
	private Boolean isDeleted = false;
	

	public RestoVO() {
		super();
	}

	public RestoVO(String restoName, String restoNameEn,Integer restoSeatsTotal, String restoInfo, String restoType,
			String restoContent, String restoPhone, String restoLoc, byte[] restoImg, Boolean isEnabled, Boolean isDeleted) {
		super();
		this.restoName = restoName;
		this.restoNameEn = restoNameEn;
		this.restoSeatsTotal = restoSeatsTotal;
		this.restoInfo = restoInfo;
		this.restoType = restoType;
		this.restoContent = restoContent;
		this.restoPhone = restoPhone;
		this.restoLoc = restoLoc;
		this.restoImg = restoImg;
		this.isEnabled = isEnabled;
		this.isDeleted = isDeleted;
	}
	
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getRestoId() {
		return restoId;
	}
	public void setRestoId(Integer restoId) {
		this.restoId = restoId;
	}
		
	public String getRestoName() {
		return restoName;
	}
	public void setRestoName(String restoName) {
		this.restoName = restoName;
	}
	
	public String getRestoNameEn() {
		return restoNameEn;
	}
	public void setRestoNameEn(String restoNameEn) {
		this.restoNameEn = restoNameEn;
	}
	
	public Integer getRestoSeatsTotal() {
		return restoSeatsTotal;
	}
	public void setRestoSeatsTotal(Integer restoSeatsTotal) {
		this.restoSeatsTotal = restoSeatsTotal;
	}
	
	public String getRestoInfo() {
		return restoInfo;
	}
	public void setRestoInfo(String restoInfo) {
		this.restoInfo = restoInfo;
	}
	
	public String getRestoType() {
		return restoType;
	}
	public void setRestoType(String restoType) {
		this.restoType = restoType;
	}
	
	public String getRestoContent() {
		return restoContent;
	}
	public void setRestoContent(String restoContent) {
		this.restoContent = restoContent;
	}
	
	public String getRestoPhone() {
		return restoPhone;
	}
	public void setRestoPhone(String restoPhone) {
		this.restoPhone = restoPhone;
	}
	
	public String getRestoLoc() {
		return restoLoc;
	}
	public void setRestoLoc(String restoLoc) {
		this.restoLoc = restoLoc;
	}
	
	public byte[] getRestoImg() {
		return restoImg;
	}
	public void setRestoImg(byte[] restoImg) {
		this.restoImg = restoImg;
	}
	
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(restoId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestoVO other = (RestoVO) obj;
		return Objects.equals(restoId, other.restoId);
	}

	@Override
	public String toString() {
		return "RestoVO [restoId=" + restoId 
				+ ", restoName=" + restoName
				+ ", restoNameEn=" + restoNameEn 
				+ ", restoSeatsTotal=" + restoSeatsTotal 
				+ ", restoInfo=" + restoInfo
				+ ", restoType=" + restoType 
				+ ", restoContent=" + (restoContent == null ? "null" : "[content length=" + restoContent.length() + "]") 
				+ ", restoPhone=" + restoPhone
				+ ", restoLoc=" + restoLoc 
				+ ", restoImg=" + (restoImg == null ? "null" : "[image bytes length=" + restoImg.length + "]")
				+ ", isEnabled=" + isEnabled
				+ ", isDeleted=" + isDeleted + "]";
	}

	
	
}
