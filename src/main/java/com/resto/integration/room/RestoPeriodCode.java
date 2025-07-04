package com.resto.integration.room;

public enum RestoPeriodCode {
	BREAKFAST(0, "早餐", "info"),
	BRUNCH(1, "早午餐", "yuko-secondary"),
	LUNCH(2, "午餐", "warning"),
	TEA(3, "午茶", "yuko-accent"),
	DINNER(4, "晚餐", "yuko-primary"),
	SUPPER(5, "宵夜", "dark");
	
    private final int code;
    private final String label;
    private final String css;

    RestoPeriodCode(int code, String label, String css) {
        this.code = code;
        this.label = label;
        this.css = css;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return css;
    }

    public static RestoPeriodCode fromCode(int code) {
        for (RestoPeriodCode s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown RestoPeriodCode code: " + code);
    }
}
