package com.shopOrdDet.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShopOrdDetRepository extends JpaRepository<ShopOrdDetVO, ShopOrdDetIdVO>{
	
	// 可選：查某筆訂單所有明細
    List<ShopOrdDetVO> findByShopOrdVO_ProdOrdId(Integer prodOrdId);

    // 查詢單一明細
    Optional<ShopOrdDetVO> findByPpid_ProdOrdIdAndPpid_ProductId(Integer prodOrdId, Integer productId);

    // 刪除單一明細
    @Transactional
    @Modifying
    @Query("DELETE FROM ShopOrdDetVO s WHERE s.ppid.prodOrdId = :prodOrdId AND s.ppid.productId = :productId")
    void deleteByPpid_ProdOrdIdAndPpid_ProductId(@Param("prodOrdId") Integer prodOrdId, @Param("productId") Integer productId);

}
