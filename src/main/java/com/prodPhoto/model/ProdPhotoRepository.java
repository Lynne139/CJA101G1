package com.prodPhoto.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prodPhoto.model.*;

@Repository
public interface ProdPhotoRepository extends JpaRepository<ProdPhotoVO, Integer> {
	
	@Transactional
	@Modifying
	@Query("delete from ProdPhotoVO where prodPhotoId = ?1")
	void deleteByProdPhotoId(int prodPhotoId);
	//● (自訂)條件查詢
	@Query("from ProdPhotoVO where prodVO.productId = ?1 order by prodPhotoId")
	List<ProdPhotoVO> findByOthers(Integer productId);

}
