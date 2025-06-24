package com.coupon.enums;

public enum OrderType {
    ROOM_ONLY(1, "限訂房"),
    PROD_ONLY(2, "限商城"),
    ROOM_AND_PROD(3, "訂房和商城");

    private final int value;
    private final String label;

    OrderType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    // 把資料庫中的整數值（如 1、2、3）轉換成對應的 OrderType Enum 實例
    public static OrderType fromValue(int value) {
        for (OrderType type : OrderType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown OrderType value: " + value);
    }
}
