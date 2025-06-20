// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.prod.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prodCate.model.ProdCateVO;

@Repository
public interface ProdRepository extends JpaRepository<ProdVO, Integer> {

	//● (自訂)條件查詢
	@Query("from ProdVO where productId = ?1 and prodCateVO.prodCateId = ?2 and productName like ?3 order by productId")
    List<ProdVO> findByOthers(Integer productId, Integer prodCateId, String productName);
}