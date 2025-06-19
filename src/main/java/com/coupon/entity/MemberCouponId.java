package com.coupon.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MemberCouponId implements Serializable {

    private String couponCode;
    private Integer memberId;

    // Constructors
    public MemberCouponId() {}

    public MemberCouponId(String couponCode, Integer memberId) {
        this.couponCode = couponCode;
        this.memberId = memberId;
    }

    // Getters & Setters

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    // equals() & hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberCouponId)) return false;
        MemberCouponId that = (MemberCouponId) o;
        return Objects.equals(couponCode, that.couponCode) &&
               Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(couponCode, memberId);
    }
}
