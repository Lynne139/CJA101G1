package com.shopOrdDet.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ShopOrdDetIdVO implements Serializable{
	
	@Column(name = "PRODUCT_ORDER_ID")
    private Integer prodOrdId;

    @Column(name = "PRODUCT_ID")
    private Integer productId;
    
    
	public ShopOrdDetIdVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public ShopOrdDetIdVO(Integer prodOrdId, Integer productId) {
		super();
		this.prodOrdId = prodOrdId;
		this.productId = productId;
	}
	
	



	public Integer getProdOrdId() {
		return prodOrdId;
	}



	public void setProdOrdId(Integer prodOrdId) {
		this.prodOrdId = prodOrdId;
	}



	public Integer getProductId() {
		return productId;
	}



	public void setProductId(Integer productId) {
		this.productId = productId;
	}



	@Override
	public int hashCode() {
		return Objects.hash(prodOrdId, productId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShopOrdDetIdVO other = (ShopOrdDetIdVO) obj;
		return Objects.equals(prodOrdId, other.prodOrdId) && Objects.equals(productId, other.productId);
	}
    
    

}
