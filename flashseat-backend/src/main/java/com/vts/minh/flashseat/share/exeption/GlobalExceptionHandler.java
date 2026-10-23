package com.vts.minh.flashseat.share.exeption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleException(ResponseStatusException exception){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("Status", exception.getStatusCode().value());
        map.put("Message", exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(map);
    }
}
