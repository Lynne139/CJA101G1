package com.coupon.repository;

import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCoupon;
import com.coupon.entity.MemberCouponId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, MemberCouponId> {

    // 1. 讓會員查詢已使用的 coupon
    @Query(value = """
            SELECT c.* FROM member_coupon mc
            JOIN coupon c ON mc.coupon_code = c.coupon_code
            WHERE mc.member_id = :memberId
              AND mc.is_used = true
            """, nativeQuery = true)
    List<Coupon> findUsedCouponsByMemberId(@Param("memberId") Integer memberId);

    // 2. 讓會員查詢未使用、指定 orderType、未過期的 coupon
    @Query(value = """
            SELECT c.* FROM member_coupon mc
            JOIN coupon c ON mc.coupon_code = c.coupon_code
            WHERE mc.member_id = :memberId
              AND mc.is_used = false
              AND c.order_type = :orderType
              AND c.expiry_date >= :currentDate
            """, nativeQuery = true)
    List<Coupon> findAvailableCouponsByMemberIdAndOrderType(
            @Param("memberId") Integer memberId,
            @Param("orderType") int orderType,
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
            """, nativeQuery = true)
    List<Coupon> findProdOnlyOrAllCoupons(
            @Param("memberId") Integer memberId,
            @Param("currentDate") LocalDate currentDate,
            @Param("priceBeforeUsingCoupon") Integer priceBeforeUsingCoupon);
}
