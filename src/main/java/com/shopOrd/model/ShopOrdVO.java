package com.shopOrd.model;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.coupon.entity.Coupon;
import com.member.model.MemberVO;
import com.prodCate.model.ProdCateVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "shop_order")
public class ShopOrdVO implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id //@Id代表這個屬性是這個Entity的唯一識別屬性，並且對映到Table的主鍵 
	@Column(name = "PRODUCT_ORDER_ID")  //@Column指這個屬性是對應到資料庫Table的哪一個欄位   //【非必要，但當欄位名稱與屬性名稱不同時則一定要用】
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】
	private Integer prodOrdId;

	@ManyToOne
	@JoinColumn(name = "MEMBER_ID",referencedColumnName = "MEMBER_ID", nullable = false)   // 指定用來join table的column
	private MemberVO memberVO;
	
	// PRODUCT_ORDER_DATE在DB中已寫DATETIME DEFAULT CURRENT_TIMESTAMP，故insertable = false
    @Column(name = "PRODUCT_ORDER_DATE", nullable = false, updatable = false, insertable = false)
	private LocalDateTime prodOrdDate;
	
    @Column(name = "PRODUCT_AMOUNT" , nullable = false, updatable = false, insertable = false)
    private  Integer prodAmount;
    
    @Column(name = "PAYMENT_METHOD")
	private Boolean payMethod;
    
    @Column(name = "ORDER_STATUS")
	private Integer ordStat;
    
    @ManyToOne
    @JoinColumn(name = "COUPON_CODE")
	private Coupon coupon;
    
    @Column(name = "DISCOUNT_AMOUNT")
    private Integer discountAmount;
    
    @Column(name = "ACTUAL_PAYMENT_AMOUNT", nullable = false)
    private Integer actualPaymentAmount;

	public ShopOrdVO() {
		super();
	}

	public ShopOrdVO(Integer prodOrdId, MemberVO memberVO, LocalDateTime prodOrdDate, Integer prodAmount,
			Boolean payMethod, Integer ordStat, Coupon coupon, Integer discountAmount, Integer actualPaymentAmount) {
		super();
		this.prodOrdId = prodOrdId;
		this.memberVO = memberVO;
		this.prodOrdDate = prodOrdDate;
		this.prodAmount = prodAmount;
		this.payMethod = payMethod;
		this.ordStat = ordStat;
		this.coupon = coupon;
		this.discountAmount = discountAmount;
		this.actualPaymentAmount = actualPaymentAmount;
	}

	public Integer getProdOrdId() {
		return prodOrdId;
	}

	public void setProdOrdId(Integer prodOrdId) {
		this.prodOrdId = prodOrdId;
	}

	public MemberVO getMemberVO() {
		return memberVO;
	}

	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}

	public LocalDateTime getProdOrdDate() {
		return prodOrdDate;
	}

	public void setProdOrdDate(LocalDateTime prodOrdDate) {
		this.prodOrdDate = prodOrdDate;
	}

	public Integer getProdAmount() {
		return prodAmount;
	}

	public void setProdAmount(Integer prodAmount) {
		this.prodAmount = prodAmount;
	}

	public Boolean getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(Boolean payMethod) {
		this.payMethod = payMethod;
	}

	public Integer getOrdStat() {
		return ordStat;
	}

	public void setOrdStat(Integer ordStat) {
		this.ordStat = ordStat;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public Integer getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Integer discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Integer getActualPaymentAmount() {
		return actualPaymentAmount;
	}

	public void setActualPaymentAmount(Integer actualPaymentAmount) {
		this.actualPaymentAmount = actualPaymentAmount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
	
   
	

}
