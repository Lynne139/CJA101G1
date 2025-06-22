package com.coupon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.member.model.MemberVO;

@Entity
@Table(name = "member_coupon")
public class MemberCoupon {

    @EmbeddedId
    private MemberCouponId id; // 複合主鍵id

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false; // 已被使用

    // claim_time在DB中已寫DATETIME DEFAULT CURRENT_TIMESTAMP，故insertable = false
    @Column(name = "claim_time", nullable = false, updatable = false, insertable = false)
    private LocalDateTime claimTime; // 領券時間

    @Column(name = "used_time")
    private LocalDateTime usedTime; // 使用時間

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("couponCode")
    @JoinColumn(name = "coupon_code", referencedColumnName = "coupon_code")
    private Coupon coupon; // 折價券

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberVO member; // 會員

    // Getters & Setters

    public MemberCouponId getId() {
        return id;
    }

    public void setId(MemberCouponId id) {
        this.id = id;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getClaimTime() {
        return claimTime;
    }

    public LocalDateTime getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(LocalDateTime usedTime) {
        this.usedTime = usedTime;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public MemberVO getMember() {
        return member;
    }

    public void setMember(MemberVO member) {
        this.member = member;
    }
}
