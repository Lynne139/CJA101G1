package com.member.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

  

@Entity
@Table(name = "member")
public class MemberVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer memberId;
	private String memberLevel;
	private String memberPassword;
	private String memberName;
	private LocalDate memberBirthday;
	private String memberPhone;
	private String memberAddress;
	private byte[] memberPic;
	private Integer memberStatus;
	private String memberEmail;
	private Integer memberPoints;
	private Integer memberAccumulativeConsumption;
	
	private MultipartFile uploadPic;
	
	public MemberVO() {//必需有一個不傳參數建構子
		
	}
	
	@Id //@Id代表這個屬性是這個Entity的唯一識別屬性，並且對映到Table的主鍵
	@Column(name = "member_id")  //@Column指這個屬性是對應到資料庫Table的哪一個欄位   //【非必要，但當欄位名稱與屬性名稱不同時則一定要用】
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】 
//	@OneToMany(mappedBy = "roomTypeVO", cascade = CascadeType.ALL)
	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	
	@Column(name = "member_level")   
	@NotEmpty(message="會員等級: 請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,30}$", message = "會員等級: 只能是中文")
	public String getMemberLevel() {
		return memberLevel;
	}
	
	public void setMemberLevel(String memberLevel) {
		this.memberLevel = memberLevel;
	}
	
	@Column(name = "member_password")
	@NotEmpty(message="密碼: 請勿空白")
	@Pattern(regexp = "^[a-zA-Z0-9_]{1,50}$", message = "密碼: 只能是英文字母、數字和_ , 且長度必需在1到50之間")
	public String getMemberPassword() {
		return memberPassword;
	}
	
	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}
	
	@Column(name = "member_name")
	@NotEmpty(message="會員名稱: 請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{1,50}$", message = "會員名稱: 只能是中、英文字母、數字和_ , 且長度必需在1到50之間")
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	@Column(name = "member_birthday")
	@NotNull(message="生日日期: 請勿空白")	
	@Past(message="日期必須是在今日(含)之前")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public LocalDate getMemberBirthday() { 
		return memberBirthday; 
	} 
	
	public void setMemberBirthday(LocalDate memberBirthday) {
		this.memberBirthday = memberBirthday;
	}
	
	@Column(name = "member_phone")
	@NotEmpty(message="電話: 請勿空白")
	@Pattern(regexp = "^\\d{6,12}$", message = "電話: 只能是數字")
	public String getMemberPhone() {
		return memberPhone;
	}
	
	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}
	
	
	@Column(name = "member_address")
	@NotEmpty(message="地址: 請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$", message = "地址: 只能是中、英文字母、數字和_")
	@Size(min = 1, max = 255, message = "地址: 長度需在1到255之間")
	public String getMemberAddress() {
		return memberAddress;
	}
	
	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}
	
	@Column(name = "member_pic")
	public byte[] getMemberPic() {
		return memberPic;
	}
	
	public void setMemberPic(byte[] memberPic) {
		this.memberPic = memberPic;
	}
	
	@Column(name = "member_status")
	@NotNull(message="會員狀態: 請勿空白")
	@Max(value = 2, message = "會員狀態 1:啟用 2:停權")
	@Min(value = 1, message = "會員狀態 1:啟用 2:停權")
	public Integer getMemberStatus() {
		return memberStatus;
	}
	
	public void setMemberStatus(Integer memberStatus) {
		this.memberStatus = memberStatus;
	}
	
	@NotEmpty(message="電子信箱: 請勿空白")
	@Size(max = 50, message = "電子信箱長度不能超過50個字")
	@Column(name = "member_email")
	@Email(message = "電子信箱: 格式錯誤")
	public String getMemberEmail() {
		return memberEmail;
	}
	
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	
	@Column(name = "member_points")
	@NotNull(message="會員積分: 請勿空白")
	@Min(value = 0, message = "會員積分: 不能小於{value}")
	public Integer getMemberPoints() {
		return memberPoints;
	}
	
	public void setMemberPoints(Integer memberPoints) {
		this.memberPoints = memberPoints;
	}
	
	@Column(name = "member_accumulative_consumption")
	@NotNull(message="會員累積消費金額: 請勿空白")
	@Min(value = 0, message = "會員累積消費金額: 不能小於{value}")
	public Integer getMemberAccumulativeConsumption() {
		return memberAccumulativeConsumption;
	}
	
	public void setMemberAccumulativeConsumption(Integer memberAccumulativeConsumption) {
		this.memberAccumulativeConsumption = memberAccumulativeConsumption;
	}
	
	@Transient
	public MultipartFile getUploadPic() {
		return uploadPic;
	}

	public void setUploadPic(MultipartFile uploadPic) {
		this.uploadPic = uploadPic;
	}
}
