package com.shopOrd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.member.model.MemberVO;

@Repository
public interface ShopOrdRepository extends JpaRepository<ShopOrdVO, Integer>{
	
	//● (自訂)條件查詢
		@Query("from ShopOrdVO where prodOrdId = ?1 and memberVO.memberId = ?2 order by prodOrdId")
	    List<ShopOrdVO> findByOthers(Integer prodOrdId, Integer memberId);
        
//		@Query("FROM ShopOrdVO s WHERE s.prodOrdId = ?1 AND s.member.memberId = ?2 ORDER BY s.prodOrdId")
//		List<ShopOrdVO> findByOthers(Integer prodOrdId, Integer memberId);
}
