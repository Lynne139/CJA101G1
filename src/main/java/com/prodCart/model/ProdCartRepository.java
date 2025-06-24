package com.prodCart.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProdCartRepository extends JpaRepository<ProdCartVO, ProdMemberIdVO> {

    // 查詢某個會員的購物車全部商品
    List<ProdCartVO> findByMemberVO_MemberId(Integer memberId);

    // 查詢某會員是否已加入某商品
    Optional<ProdCartVO> findByPmid_ProductIdAndPmid_MemberId(Integer productId, Integer memberId);

    // 刪除某會員購物車的所有商品
    @Transactional
    @Modifying
    @Query("delete from ProdCartVO where memberVO.memberId = ?1")
    void deleteByMemberVO_MemberId(Integer memberId);

    // 刪除某會員購物車中指定商品
    @Transactional
    @Modifying
    @Query("delete from ProdCartVO where pmid.productId = ?1 and pmid.memberId = ?2")
    void deleteByPmid_ProductIdAndPmid_MemberId(Integer productId, Integer memberId);

}