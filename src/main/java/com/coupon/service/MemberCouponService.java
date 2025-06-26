package com.coupon.service;

import com.coupon.entity.Coupon;
import com.coupon.entity.MemberCoupon;
import com.coupon.entity.MemberCouponId;
import com.coupon.enums.OrderType;
import com.coupon.repository.MemberCouponRepository;
import com.coupon.repository.CouponRepository;
import com.member.model.MemberVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponRepository couponRepository;

    @Autowired
    public MemberCouponService(MemberCouponRepository memberCouponRepository,
                               CouponRepository couponRepository) {
        this.memberCouponRepository = memberCouponRepository;
        this.couponRepository = couponRepository;
    }

    // 1. 查詢會員已使用的折價券
    public List<Coupon> getUsedCouponsByMember(Integer memberId) {
        return memberCouponRepository.findUsedCouponsByMemberId(memberId);
    }

    // 2. 查詢會員可用於訂房或商城、未過期、未使用的折價券
    public List<Coupon> getAvailableCouponsByOrderTypes(Integer memberId, OrderType selectedOrderType) {
        List<Integer> orderTypes;

        // 根據前端選擇的訂單類型組成對應的可接受類型清單
        if (selectedOrderType == OrderType.ROOM_ONLY) {
            // 前端選擇「訂房」時，允許 ROOM_ONLY 和 ROOM_AND_PROD
            orderTypes = List.of(OrderType.ROOM_ONLY.getValue(), OrderType.ROOM_AND_PROD.getValue());
        } else if (selectedOrderType == OrderType.PROD_ONLY) {
            // 前端選擇「商城」時，允許 PROD_ONLY 和 ROOM_AND_PROD
            orderTypes = List.of(OrderType.PROD_ONLY.getValue(), OrderType.ROOM_AND_PROD.getValue());
        } else {
            // 若傳入 ROOM_AND_PROD 或其他，僅回傳完全通用型
            orderTypes = List.of(OrderType.ROOM_AND_PROD.getValue());
        }

        return memberCouponRepository.findAvailableCouponsByMemberIdAndOrderTypes(
        		memberId, orderTypes, LocalDate.now());
    }

    // 3. 查詢會員可用於某筆訂房訂單的折價券
    public List<Coupon> getRoomOrderApplicableCoupons(Integer memberId, Integer priceBeforeUsingCoupon) {
        return memberCouponRepository.findRoomOnlyOrAllCoupons(
                memberId,
                LocalDate.now(),
                priceBeforeUsingCoupon
        );
    }

    // 4. 查詢會員可用於某筆商城訂單的折價券
    public List<Coupon> getProductOrderApplicableCoupons(Integer memberId, Integer priceBeforeUsingCoupon) {
        return memberCouponRepository.findProdOnlyOrAllCoupons(
                memberId,
                LocalDate.now(),
                priceBeforeUsingCoupon
        );
    }

    // 5. 新增一筆 MemberCoupon（給會員領取折價券時使用）
    public MemberCoupon addMemberCoupon(Integer memberId, String couponCode) {
        // 檢查該 coupon 是否存在
        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("查無此折價券: " + couponCode));

        // 檢查是否已領取過（避免重複領券）
        MemberCouponId id = new MemberCouponId(couponCode, memberId);
        if (memberCouponRepository.existsById(id)) {
            throw new IllegalStateException("此折價券已領取");
        }

        // 建立 MemberVO 實例，只需設定主鍵
        // (因為JPA 知道我設定的是 @ManyToOne，只要我設定了主鍵，
        // 它就能處理好關聯欄位（member_id）。)
        MemberVO member = new MemberVO();
        member.setMemberId(memberId);

        // 建立 MemberCoupon 並設定基本欄位
        MemberCoupon mc = new MemberCoupon();
        mc.setId(id);
        mc.setMember(member);
        mc.setCoupon(coupon);
        mc.setIsUsed(false); // 預設為未使用

        return memberCouponRepository.save(mc);
    }

    // 6. 使用一張折價券：設 isUsed 為 true，並更新 usedTime 為現在
    public MemberCoupon useCoupon(Integer memberId, String couponCode) {
        // 建立複合主鍵
        MemberCouponId id = new MemberCouponId(couponCode, memberId);

        // 查找 MemberCoupon 資料
        MemberCoupon mc = memberCouponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("查無此折價券或未領取"));

        // 若已使用，不可重複使用
        if (Boolean.TRUE.equals(mc.getIsUsed())) {
            throw new IllegalStateException("此折價券已使用");
        }

        // 更新狀態與使用時間
        mc.setIsUsed(true);
        mc.setUsedTime(LocalDateTime.now());

        return memberCouponRepository.save(mc);
    }

}
