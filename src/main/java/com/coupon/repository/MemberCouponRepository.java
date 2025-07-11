package com.coupon.repository;

import com.coupon.dto.CouponUsageDTO;
import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCoupon;
import com.coupon.entity.MemberCouponId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, MemberCouponId> {

    // 1. 讓會員查詢已使用的 coupon (包含使用時間) (JPQL 查詢)
    @Query("""
    	    SELECT new com.coupon.dto.CouponUsageDTO(
    	        c.couponCode, c.couponName, c.orderType, c.discountValue, mc.usedTime
    	    )
    	    FROM MemberCoupon mc
    	    JOIN mc.coupon c
    	    WHERE mc.member.id = :memberId
    	      AND mc.isUsed = true
    	    ORDER BY mc.usedTime DESC
    	""")
    	List<CouponUsageDTO> findUsedCouponUsagesByMemberId(@Param("memberId") Integer memberId);

    // 2. 讓會員查詢指定orderTypes、未使用、未過期的 coupon
    @Query(value = """
            SELECT c.* FROM member_coupon mc
            JOIN coupon c ON mc.coupon_code = c.coupon_code
            WHERE mc.member_id = :memberId
              AND mc.is_used = false
              AND c.order_type IN :orderTypes
              AND c.expiry_date >= :currentDate
            ORDER BY c.discount_value DESC
            """, nativeQuery = true)
    List<Coupon> findAvailableCouponsByMemberIdAndOrderTypes(
            @Param("memberId") Integer memberId,
            @Param("orderTypes") List<Integer> orderTypes,
            @Param("currentDate") LocalDate currentDate);

    // 3. 查詢某會員所持有，且可用於某筆訂房訂單的折價券
    @Query(value = """
            SELECT c.* FROM member_coupon mc
            JOIN coupon c ON mc.coupon_code = c.coupon_code
            WHERE mc.member_id = :memberId -- 屬於某會員
              AND mc.is_used = false -- 尚未使用
              AND (c.order_type = 1 OR c.order_type = 3) -- 可用於訂房
              AND c.expiry_date >= :currentDate -- 未過期
              AND c.min_purchase <= :priceBeforeUsingCoupon -- 符合低消門檻
            ORDER BY c.discount_value DESC
            """, nativeQuery = true)
    List<Coupon> findRoomOnlyOrAllCoupons(
            @Param("memberId") Integer memberId,
            @Param("currentDate") LocalDate currentDate,
            @Param("priceBeforeUsingCoupon") Integer priceBeforeUsingCoupon);

    // 4. 查詢某會員所持有，且可用於某筆商城訂單的折價券
    @Query(value = """
            SELECT c.* FROM member_coupon mc
            JOIN coupon c ON mc.coupon_code = c.coupon_code
            WHERE mc.member_id = :memberId -- 屬於某會員
              AND mc.is_used = false -- 尚未使用
              AND (c.order_type = 2 OR c.order_type = 3) -- 可用於商城
              AND c.expiry_date >= :currentDate -- 未過期
              AND c.min_purchase <= :priceBeforeUsingCoupon -- 符合低消門檻
            ORDER BY c.discount_value DESC
            """, nativeQuery = true)
    List<Coupon> findProdOnlyOrAllCoupons(
            @Param("memberId") Integer memberId,
            @Param("currentDate") LocalDate currentDate,
            @Param("priceBeforeUsingCoupon") Integer priceBeforeUsingCoupon);
}
