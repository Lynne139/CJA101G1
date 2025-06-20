package com.prodPhoto.model;

import com.prod.model.ProdVO;
import com.prodCate.model.ProdCateVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "PRODUCT_PHOTO")
public class ProdPhotoVO implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PRODUCT_PHOTO_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY) //@GeneratedValue的generator屬性指定要用哪個generator //【strategy的GenerationType, 有四種值: AUTO, IDENTITY, SEQUENCE, TABLE】
	private Integer prodPhotoId;
	
    // @ManyToOne  (雙向多對一/一對多) 的多對一
	//【此處預設為 @ManyToOne(fetch=FetchType.EAGER) --> 是指 lazy="false"之意】【注意: 此處的預設值與XML版 (p.127及p.132) 的預設值相反】
	//【如果修改為 @ManyToOne(fetch=FetchType.LAZY)  --> 則指 lazy="true" 之意】
	@ManyToOne
	@JoinColumn(name = "PRODUCT_ID")   // 指定用來join table的column
	private ProdVO prodVO;
	
	@Column(name = "PRODUCT_PHOTO")
	private byte[] prodPhoto;

	public ProdPhotoVO() {
	}

	

	public ProdPhotoVO(Integer prodPhotoId, ProdVO prodVO, byte[] prodPhoto) {
		super();
		this.prodPhotoId = prodPhotoId;
		this.prodVO = prodVO;
		this.prodPhoto = prodPhoto;
	}



	public Integer getProdPhotoId() {
		return prodPhotoId;
	}

	public void setProdPhotoId(Integer prodPhotoId) {
		this.prodPhotoId = prodPhotoId;
	}

	

	public ProdVO getProdVO() {
		return prodVO;
	}



	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}



	public byte[] getProdPhoto() {
		return prodPhoto;
	}

	public void setProdPhoto(byte[] prodPhoto) {
		this.prodPhoto = prodPhoto;
	}
	
	

}
