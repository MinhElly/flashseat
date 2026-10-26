package com.vts.minh.flashseat.share.exeption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleException(MethodArgumentTypeMismatchException exception){
        boolean invaildId = "id".equals(exception.getName()) && Long.class.equals(exception.getRequiredType());
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("Status", 400);
        map.put("message", invaildId ? "Invaild ID" : "Invaild parameter");

        return ResponseEntity.badRequest().body(map);
    }
}
