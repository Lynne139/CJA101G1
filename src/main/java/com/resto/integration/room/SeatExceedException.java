package com.resto.integration.room;

// 餐廳訂位超額例外
public class SeatExceedException extends RuntimeException {
    public SeatExceedException(String message) {
        super(message);
    }
}