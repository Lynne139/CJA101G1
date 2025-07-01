package com.prodPhoto.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prodPhoto.model.*;



@Service("prodPhotoService")
public class ProdPhotoService {
	
	@Autowired
	ProdPhotoRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addProdPhoto(ProdPhotoVO prodPhotoVO) {
		repository.save(prodPhotoVO);
	}

	public void updateProdPhoto(ProdPhotoVO prodPhotoVO) {
		repository.save(prodPhotoVO);
	}

	public void deleteProdPhoto(Integer prodPhotoId) {
		if (repository.existsById(prodPhotoId))
			repository.deleteById(prodPhotoId);
	}

	public ProdPhotoVO getOneProdPhoto(Integer prodPhotoId) {
		Optional<ProdPhotoVO> optional = repository.findById(prodPhotoId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<ProdPhotoVO> getAll() {
		return repository.findAll();
	}

	/**
	 * 根據商品ID查詢該商品的所有照片
	 */
	public List<ProdPhotoVO> getPhotosByProductId(Integer productId) {
		return repository.findByOthers(productId);
	}

}
