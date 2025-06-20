// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.prodCate.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProdCateRepository extends JpaRepository<ProdCateVO, Integer> {


	//● (自訂)條件查詢
	@Query("from ProdCateVO where prodCateId = ?1 and prodCateName like ?2 order by prodCateId")
	List<ProdCateVO> findByOthers(Integer prodCateId, String prodCateName);
	
}