package com.prodCart.model;

import java.io.Serializable;
import java.sql.Date;

import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCouponId;
import com.member.model.MemberVO;
import com.prod.model.ProdVO;
import com.prodCate.model.ProdCateVO;

import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Entity  
@Table(name = "prod_cart_item") 

public class ProdCartVO implements java.io.Serializable{
	
	
	@EmbeddedId
    private ProdMemberIdVO pmid;
	
	@Column(name = "QUANTITY")
	@NotNull(message = "數量: 請勿空白")
	@Min(value = 1, message = "數量: 不能小於1")
	@Max(value = 999, message = "數量: 不能超過999")
    private Integer quantity;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID", insertable = false, updatable = false)
    private ProdVO prodVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("memberId")
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "MEMBER_ID", insertable = false, updatable = false)
    private MemberVO memberVO;

	public ProdMemberIdVO getPmid() {
		return pmid;
	}

	public void setPmid(ProdMemberIdVO pmid) {
		this.pmid = pmid;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ProdVO getProdVO() {
		return prodVO;
	}

	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}

	public MemberVO getMemberVO() {
		return memberVO;
	}

	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}
    
    

}
