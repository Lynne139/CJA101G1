package com.coupon.controller;

import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCoupon;
import com.coupon.enums.OrderType;
import com.coupon.service.MemberCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    @Autowired
    public MemberCouponController(MemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    // 1. 查詢會員已使用的折價券
    @GetMapping("/{memberId}/used")
    public ResponseEntity<List<Coupon>> getUsedCoupons(@PathVariable Integer memberId) {
        List<Coupon> coupons = memberCouponService.getUsedCouponsByMember(memberId);
        return ResponseEntity.ok(coupons);
    }

	 // 2. 查詢會員可用於指定訂單類型的折價券（未使用、未過期）
	 // 支援 ROOM_ONLY 回傳 ROOM_ONLY + ROOM_AND_PROD
	 // 支援 PROD_ONLY 回傳 PROD_ONLY + ROOM_AND_PROD
	 @GetMapping("/{memberId}/usable")
	 public ResponseEntity<List<Coupon>> getAvailableCouponsByOrderTypes(
	         @PathVariable Integer memberId,
	         @RequestParam OrderType orderType) {
	     List<Coupon> coupons = memberCouponService.getAvailableCouponsByOrderTypes(memberId, orderType);
	     return ResponseEntity.ok(coupons);
	 }

    // 3. 查詢會員可用於訂房的折價券
    @GetMapping("/{memberId}/room-order-usable")
    public ResponseEntity<List<Coupon>> getRoomOrderCoupons(
            @PathVariable Integer memberId,
            @RequestParam Integer priceBeforeUsingCoupon) {
        List<Coupon> coupons = memberCouponService.getRoomOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
        return ResponseEntity.ok(coupons);
    }

    // 4. 查詢會員可用於商城的折價券
    @GetMapping("/{memberId}/product-order-usable")
    public ResponseEntity<List<Coupon>> getProductOrderCoupons(
            @PathVariable Integer memberId,
            @RequestParam Integer priceBeforeUsingCoupon) {
        List<Coupon> coupons = memberCouponService.getProductOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
        return ResponseEntity.ok(coupons);
    }

    // 5. 領取折價券
    @PostMapping("/{memberId}/claim/{couponCode}")
    public ResponseEntity<MemberCoupon> claimCoupon(
            @PathVariable Integer memberId,
            @PathVariable String couponCode) {
        MemberCoupon claimed = memberCouponService.addMemberCoupon(memberId, couponCode);
        return ResponseEntity.ok(claimed);
    }

    // 6. 使用折價券
    @PutMapping("/{memberId}/use/{couponCode}")
    public ResponseEntity<MemberCoupon> useCoupon(
            @PathVariable Integer memberId,
            @PathVariable String couponCode) {
        MemberCoupon used = memberCouponService.useCoupon(memberId, couponCode);
        return ResponseEntity.ok(used);
    }
}
