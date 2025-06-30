package com.coupon.dto;

import com.coupon.enums.OrderType;
import java.time.LocalDateTime;

public class CouponUsageDTO {

    private String couponCode;
    private String couponName;
    private OrderType orderType;
    private Integer discountValue;
    private LocalDateTime usedTime;

    // Constructor
    public CouponUsageDTO(String couponCode, String couponName, OrderType orderType,
                          Integer discountValue, LocalDateTime usedTime) {
        this.couponCode = couponCode;
        this.couponName = couponName;
        this.orderType = orderType;
        this.discountValue = discountValue;
        this.usedTime = usedTime;
    }

    // Getters
    public String getCouponCode() {
        return couponCode;
    }

    public String getCouponName() {
        return couponName; 
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public Integer getDiscountValue() {
        return discountValue;
    }

    public LocalDateTime getUsedTime() {
        return usedTime;
    }
}