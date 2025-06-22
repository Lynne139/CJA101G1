package com.prod.model;

import java.sql.Date;

import com.prodCate.model.ProdCateVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Entity  //要加上@Entity才能成為JPA的一個Entity類別
@Table(name = "product") //代表這個class是對應到資料庫的實體table，目前對應的table是EMP2 
public class ProdVO implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id //@Id代表這個屬性是這個Entity的唯一識別屬性，並且對映到Table的主鍵 
	@Column(name = "PRODUCT_ID")  //@Column指這個屬性是對應到資料庫Table的哪一個欄位   //【非必要，但當欄位名稱與屬性名稱不同時則一定要用】
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】
	private Integer productId;
	
    // @ManyToOne  (雙向多對一/一對多) 的多對一
	//【此處預設為 @ManyToOne(fetch=FetchType.EAGER) --> 是指 lazy="false"之意】【注意: 此處的預設值與XML版 (p.127及p.132) 的預設值相反】
	//【如果修改為 @ManyToOne(fetch=FetchType.LAZY)  --> 則指 lazy="true" 之意】
	@ManyToOne
	@JoinColumn(name = "PRODUCT_CATEGORY_ID")   // 指定用來join table的column
	private ProdCateVO prodCateVO;
	
	@NotNull(message="商品價格: 請勿空白")
	@Min(value = 1, message = "商品價格: 不能小於{value}")
	@Max(value = 99999, message = "商品價格: 不能超過{value}")
	@Column(name = "PRODUCT_PRICE")
	private Integer productPrice;
	

	@NotEmpty(message="商品名稱: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "商品名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	@Column(name = "PRODUCT_NAME")
	private String productName;
	
	
	@Column(name = "PRODUCT_STATUS")
	private Boolean productStatus;
	

	public Integer getProductId() {
		return productId;
	}
	
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public ProdCateVO getProdCateVO() {
		return prodCateVO;
	}

	public void setProdCateVO(ProdCateVO prodCateVO) {
		this.prodCateVO = prodCateVO;
	}

	public Integer getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Boolean getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(Boolean productStatus) {
		this.productStatus = productStatus;
	}
	
	
	public ProdVO() {
		super();
	}
	


	
	
	
	
	

}
