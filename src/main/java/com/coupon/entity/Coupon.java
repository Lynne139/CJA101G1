package com.coupon.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @Column(name = "coupon_code", length = 8, nullable = false)
    private String couponCode; // 折價券編號

    @Column(name = "coupon_name", length = 50, nullable = false) //最大長度是 50 個字元
    private String couponName; // 折價券名稱

    @Column(name = "order_type", nullable = false)
    private Integer orderType; // 適用的訂單種類

    @Column(name = "discount_value", nullable = false)
    private Integer discountValue; // 折價金額

    @Column(name = "min_purchase", nullable = false)
    private Integer minPurchase = 0; // 使用低銷

    @Column(name = "claim_start_date", nullable = false)
    private LocalDate claimStartDate; // 開放領取日期

    @Column(name = "claim_end_date", nullable = false)
    private LocalDate claimEndDate; // 結束領取日期

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate; // 到期日

    // created_at在DB中已寫DEFAULT CURRENT_TIMESTAMP，故insertable = false
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt; // 建立時間

    // Getters and Setters

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(Integer minPurchase) {
        this.minPurchase = minPurchase;
    }

    public LocalDate getClaimStartDate() {
        return claimStartDate;
    }

    public void setClaimStartDate(LocalDate claimStartDate) {
        this.claimStartDate = claimStartDate;
    }

    public LocalDate getClaimEndDate() {
        return claimEndDate;
    }

    public void setClaimEndDate(LocalDate claimEndDate) {
        this.claimEndDate = claimEndDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 不需要 setter for createdAt，讓它自動產生
}
