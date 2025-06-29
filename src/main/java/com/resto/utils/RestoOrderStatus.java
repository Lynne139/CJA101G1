package com.resto.utils;

public enum RestoOrderStatus {
    CANCELED(0, "已取消", "secondary"),
    CREATED(1, "已成立", "yuko-accent"),
    DONE(2, "已完成", "success"),
    WITHHOLD(3, "保留", "warning"),
    NOSHOW(4, "未到", "danger");

    private final int value;
    private final String label;
    private final String css;

    RestoOrderStatus(int value, String label, String css) {
        this.value = value;
        this.label = label;
        this.css = css;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return css;
    }

    public static RestoOrderStatus fromValue(int value) {
        for (RestoOrderStatus status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
