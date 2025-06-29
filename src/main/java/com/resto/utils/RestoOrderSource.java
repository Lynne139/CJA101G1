package com.resto.utils;

public enum RestoOrderSource {
	MEMBER(0),   // 會員登入預約
    ROOM(1),    // 住房訂單
    ADMIN(2);       // 管理員建置
    
	
	// 實際存進資料庫的代碼
    private final int code;

    RestoOrderSource(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // 方便從 int 反查 enum
    public static RestoOrderSource fromCode(int code) {
        for (RestoOrderSource s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}


