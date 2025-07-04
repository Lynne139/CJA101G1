package com.resto.utils;

public enum RestoOrderStatus {
    CANCELED(0, "已取消", "secondary"), //不佔位
    CREATED(1, "已成立", "yuko-accent"), //佔位
    DONE(2, "已完成", "success"), //不佔位（用餐完成即釋放）
    WITHHOLD(3, "保留", "warning"), //佔位
    NOSHOW(4, "未到", "danger"); //不佔位

    private final int value;
    private final String label;
    private final String css;

    RestoOrderStatus(int value, String label, String css) {
        this.value = value;
        this.label = label;
        this.css = css;
    }
    
    // 判斷此狀態是否占名額
    public boolean isCountable(){
        return this == CREATED || this == WITHHOLD;
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