package com.coupon.service;

import com.coupon.entity.Coupon;
import com.coupon.enums.OrderType;
import com.coupon.repository.CouponRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Autowired
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // 1. 管理員依 couponCode 查詢
    public Optional<Coupon> getCouponByCode(String couponCode) {
        return couponRepository.findById(couponCode);
    }

    // 2. 管理員依 couponName 查詢
    public List<Coupon> getCouponsByName(String couponName) {
        return couponRepository.findByCouponName(couponName);
    }

    // 3. 管理員依 createdAt 日期區間查詢
    public List<Coupon> getCouponsByCreatedDateRange(LocalDateTime start, LocalDateTime end) {
        return couponRepository.findByCreatedAtBetween(start, end);
    }

    // 4. 管理員修改某筆 Coupon（couponCode 必須已存在）
    public Coupon updateCoupon(Coupon updatedCoupon) {
        if (!couponRepository.existsById(updatedCoupon.getCouponCode())) {
            throw new IllegalArgumentException("找不到指定的 couponCode: " + updatedCoupon.getCouponCode());
        }
        return couponRepository.save(updatedCoupon);
    }

    // 5. 管理員新增一筆 Coupon（couponCode 不可重複）
    public Coupon createCoupon(Coupon newCoupon) {
        if (couponRepository.existsById(newCoupon.getCouponCode())) {
            throw new IllegalArgumentException("couponCode 已存在，請使用其他代碼");
        }
        return couponRepository.save(newCoupon);
    }

    // 6. 會員依 orderType 查詢他可領且尚未領取過的優惠券（使用 native SQL）
    public List<Coupon> getClaimableCouponsForMember(OrderType orderType, Integer memberId, Integer memberPoints) {
        return couponRepository.findClaimableCouponsNotClaimedByMember(
                orderType.getValue(),
                memberPoints,
                LocalDate.now(),
                memberId
        );
    }
}
