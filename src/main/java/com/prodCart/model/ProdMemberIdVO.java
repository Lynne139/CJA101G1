package com.prodCart.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProdMemberIdVO implements Serializable{
	
	@Column(name = "PRODUCT_ID", nullable = false)
    private Integer productId;

    @Column(name = "MEMBER_ID", nullable = false)
    private Integer memberId;
    
    
    // Constructors
	public ProdMemberIdVO() {
	}

	public ProdMemberIdVO(Integer productId, Integer memberId) {
		super();
		this.productId = productId;
		this.memberId = memberId;
	}

	
	// Getters & Setters
	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	
	
	// equals() & hashCode()
	
	@Override
	public int hashCode() {
		return Objects.hash(memberId, productId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdMemberIdVO other = (ProdMemberIdVO) obj;
		return Objects.equals(memberId, other.memberId) && Objects.equals(productId, other.productId);
	}
	
	    

}
