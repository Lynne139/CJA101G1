package com.resto.utils;

public enum RestoOrderSource {
    MEMBER(0, "會員", "yuko-accent"),
    ROOM(1, "住宿", "warning"),
    ADMIN(2, "後台", "yuko-primary");

    private final int code;
    private final String label;
    private final String css;

    RestoOrderSource(int code, String label, String css) {
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

    public static RestoOrderSource fromCode(int code) {
        for (RestoOrderSource s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown RestoOrderSource code: " + code);
    }
}