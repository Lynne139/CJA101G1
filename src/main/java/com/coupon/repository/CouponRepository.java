package com.coupon.repository;

import com.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, String> {

    // 1. 讓管理員依 couponCode 查詢
    Coupon findByCouponCode(String couponCode);

    // 2. 讓管理員依 couponName 查詢
    List<Coupon> findByCouponName(String couponName);

    // 3. 讓管理員依 createdAt 查詢
    List<Coupon> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 4. 讓會員依 orderType 查詢他可領且尚未領取過的coupon（使用 native SQL）
    @Query(
    	    value = """
    	        SELECT * FROM coupon
    	        WHERE order_type = :orderType
    	          AND discount_value <= :memberPoints // 因為一點可抵一元
    	          AND claim_start_date <= :today
    	          AND claim_end_date >= :today
    	          AND coupon_code NOT IN (
    	              SELECT coupon_code FROM member_coupon WHERE member_id = :memberId
    	          )
    	    """,
    	    nativeQuery = true
    	)
	List<Coupon> findClaimableCouponsNotClaimedByMember(
	    @Param("orderType") int orderType,
	    @Param("memberPoints") int memberPoints,
	    @Param("today") LocalDate today,
	    @Param("memberId") Integer memberId
	);

}
