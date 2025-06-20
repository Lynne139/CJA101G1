package com.prodCate.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//import org.hibernate.validator.constraints.NotEmpty;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/*
 * 註1: classpath必須有javax.persistence-api-x.x.jar 
 * 註2: Annotation可以添加在屬性上，也可以添加在getXxx()方法之上
 */

@Entity  //要加上@Entity才能成為JPA的一個Entity類別
@Table(name = "product_category") //代表這個class是對應到資料庫的實體table，目前對應的table是EMP2 
public class ProdCateVO implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PRODUCT_CATEGORY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】
	private Integer prodCateId;
	
	@Column(name = "PRODUCT_CATEGORY_NAME")
	@NotEmpty(message="商品類別名稱: 請勿空白")
//	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,30}$", message = "類別名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到30之間")
	private String prodCateName;
	
	@Column(name = "PRODUCT_CATEGORY_DESC")
//	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,1000}$", message = "員工姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到1000之間")
	private String prodCateDesc;
	
	public ProdCateVO() {
	}
	
	public ProdCateVO(Integer prodCateId, String prodCateName, String prodCateDesc) {
		super();
		
		this.prodCateId = prodCateId;
		this.prodCateName = prodCateName;
		this.prodCateDesc = prodCateDesc;
	}


	
	
	public Integer getProdCateId() {
		return prodCateId;
	}

	public void setProdCateId(Integer prodCateId) {
		this.prodCateId = prodCateId;
	}
	
	
	public String getProdCateName() {
		return prodCateName;
	}

	public void setProdCateName(String prodCateName) {
		this.prodCateName = prodCateName;
	}
    
	
	public String getProdCateDesc() {
		return prodCateDesc;
	}

	public void setProdCateDesc(String prodCateDesc) {
		this.prodCateDesc = prodCateDesc;
	}
	
	


}
