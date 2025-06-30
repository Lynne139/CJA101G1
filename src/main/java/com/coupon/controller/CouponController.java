package com.coupon.controller;

import com.coupon.entity.Coupon;
import com.coupon.enums.OrderType;
import com.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 1. 依 couponCode 查詢
    @GetMapping("/code/{couponCode}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String couponCode) {
        Optional<Coupon> coupon = couponService.getCouponByCode(couponCode);
        return coupon.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        // 註：傳給前端時，預設 Jackson 會把 enum 的名稱變成字串來傳 e.g."ROOM_ONLY"
    }

    // 2. 依 couponName 查詢
    @GetMapping("/name/{couponName}")
    public ResponseEntity<List<Coupon>> getCouponsByName(@PathVariable String couponName) {
        List<Coupon> coupons = couponService.getCouponsByName(couponName);
        return ResponseEntity.ok(coupons);
    }

    // 3. 依 createdAt 日期區間查詢
    @GetMapping("/created-range")
    public ResponseEntity<List<Coupon>> getCouponsByCreatedDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Coupon> coupons = couponService.getCouponsByCreatedDateRange(start, end);
        return ResponseEntity.ok(coupons);
    }

    // 4. 修改 Coupon
    @PutMapping
    public ResponseEntity<Coupon> updateCoupon(@RequestBody Coupon updatedCoupon) {
        try {
            Coupon savedCoupon = couponService.updateCoupon(updatedCoupon);
            return ResponseEntity.ok(savedCoupon);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 5. 新增 Coupon
    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon newCoupon) {
        try {
            Coupon savedCoupon = couponService.createCoupon(newCoupon);
            return ResponseEntity.ok(savedCoupon);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body("此折價券編號已存在，請改用新的編號");
        }
    }

    // 6. 讓會員查詢可領且尚未領取過的優惠券
    @GetMapping("/claimable")
    public ResponseEntity<List<Coupon>> getClaimableCouponsForMember(
            @RequestParam OrderType orderType,
            @RequestParam Integer memberId,
            @RequestParam Integer memberPoints) {
        List<Coupon> coupons = couponService.getClaimableCouponsForMember(orderType, memberId, memberPoints);
        return ResponseEntity.ok(coupons);
    }
}
