package com.shopOrdDet.model;

import com.prod.model.ProdVO;
import com.shopOrd.model.ShopOrdVO;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "SHOP_ORDER_DETAIL")
public class ShopOrdDetVO {
	
	@EmbeddedId
    private ShopOrdDetIdVO ppid;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("prodOrdId")
    @JoinColumn(name = "PRODUCT_ORDER_ID")
    private ShopOrdVO shopOrdVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID")
    private ProdVO prodVO;
    
    @NotNull(message = "購買價格: 請勿空白")
    @Min(value = 0, message = "購買價格: 不能小於0")
    @Column(name = "PURCHASE_PRICE", nullable = false)
    private Integer purchasePrice;
    
    @NotNull(message = "數量: 請勿空白")
	@Min(value = 1, message = "數量: 不能小於1")
	@Max(value = 999, message = "數量: 不能超過999")
    @Column(name = "PRODUCT_QUANTITY", nullable = false)
    private Integer prodQuantity;


	public ShopOrdDetIdVO getPpid() {
		return ppid;
	}


	public void setPpid(ShopOrdDetIdVO ppid) {
		this.ppid = ppid;
	}


	public ShopOrdVO getShopOrdVO() {
		return shopOrdVO;
	}


	public void setShopOrdVO(ShopOrdVO shopOrdVO) {
		this.shopOrdVO = shopOrdVO;
	}


	public ProdVO getProdVO() {
		return prodVO;
	}


	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}

	public Integer getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Integer purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Integer getProdQuantity() {
		return prodQuantity;
	}


	public void setProdQuantity(Integer prodQuantity) {
		this.prodQuantity = prodQuantity;
	}
    
    

}
