package com.coupon.controller;

import com.coupon.dto.CouponUsageDTO;
import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCoupon;
import com.coupon.enums.OrderType;
import com.coupon.service.MemberCouponService;
import com.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

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
    @GetMapping("/used")
    public ResponseEntity<List<CouponUsageDTO>> getUsedCoupons(HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
    	Integer memberId = loggedInMember.getMemberId();
    	List<CouponUsageDTO> dtos = memberCouponService.getUsedCouponUsagesByMember(memberId);
        return ResponseEntity.ok(dtos);
    }

	 // 2. 查詢會員可用於指定訂單類型的折價券（未使用、未過期）
	 // 支援 ROOM_ONLY 回傳 ROOM_ONLY + ROOM_AND_PROD
	 // 支援 PROD_ONLY 回傳 PROD_ONLY + ROOM_AND_PROD
	 @GetMapping("/usable")
	 public ResponseEntity<List<Coupon>> getAvailableCouponsByOrderTypes(
	         @RequestParam OrderType orderType,
	         HttpSession session) {
		 MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
	     Integer memberId = loggedInMember.getMemberId();
	     List<Coupon> coupons = memberCouponService.getAvailableCouponsByOrderTypes(memberId, orderType);
	     return ResponseEntity.ok(coupons);
	 }

    // 3. 查詢會員可用於訂房的折價券
    @GetMapping("/room-order-usable")
    public ResponseEntity<List<Coupon>> getRoomOrderCoupons(
            @RequestParam Integer priceBeforeUsingCoupon,
	        HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
	    Integer memberId = loggedInMember.getMemberId();
        List<Coupon> coupons = memberCouponService.getRoomOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
        return ResponseEntity.ok(coupons);
    }

    // 4. 查詢會員可用於商城的折價券
    @GetMapping("/product-order-usable")
    public ResponseEntity<List<Coupon>> getProductOrderCoupons(
            @RequestParam Integer priceBeforeUsingCoupon,
	        HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
	    Integer memberId = loggedInMember.getMemberId();
        List<Coupon> coupons = memberCouponService.getProductOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
        return ResponseEntity.ok(coupons);
    }

    // 5. 領取折價券
    @PostMapping("/claim/{couponCode}")
    public ResponseEntity<MemberCoupon> claimCoupon(
            @PathVariable String couponCode,
            HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
    	Integer memberId = loggedInMember.getMemberId();
        MemberCoupon claimed = memberCouponService.addMemberCoupon(memberId, couponCode);
        return ResponseEntity.ok(claimed);
    }

    // 6. 使用折價券
    @PutMapping("/use/{couponCode}")
    public ResponseEntity<MemberCoupon> useCoupon(
            @PathVariable String couponCode,
	        HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
	    Integer memberId = loggedInMember.getMemberId();
        MemberCoupon used = memberCouponService.useCoupon(memberId, couponCode);
        return ResponseEntity.ok(used);
    }
}
