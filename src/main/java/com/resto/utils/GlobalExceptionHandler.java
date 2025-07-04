package com.resto.utils;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.resto.integration.room.BadParameterException;
import com.resto.integration.room.SeatExceedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SeatExceedException.class)
    public ResponseEntity<?> seatExceed(SeatExceedException ex){
        return ResponseEntity.badRequest()
              .body(Map.of("success", false, "code", "SEATS_EXCEED", "msg", ex.getMessage()));
    }

    @ExceptionHandler(BadParameterException.class)
    public ResponseEntity<?> badParam(BadParameterException ex){
        return ResponseEntity.badRequest()
              .body(Map.of("success", false, "code", "BAD_PARAM", "msg", ex.getMessage()));
    }
}
