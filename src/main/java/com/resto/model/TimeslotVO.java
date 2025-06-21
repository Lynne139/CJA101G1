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
public class TimeslotVO implements java.io.Serializable{
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
	

	

	
	
}
