package com.resto.utils;

public enum RestoOrderStatus {
	CANCELED(0),   // 已取消
    CREATED(1),    // 已建立 / 未付款
    DONE(2),       // 已完成
    WITHHOLD(3),   // 保留（例如刷卡預授權）
    NOSHOW(4);     // 未到
	
	// 實際存進資料庫的代碼
    private final int code;

    RestoOrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // 方便從 int 反查 enum
    public static RestoOrderStatus fromCode(int code) {
        for (RestoOrderStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}


