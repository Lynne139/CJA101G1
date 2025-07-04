package com.resto.integration.room;

// 餐廳參數錯誤例外
public class BadParameterException extends RuntimeException {
    public BadParameterException(String message) {
        super(message);
    }
}