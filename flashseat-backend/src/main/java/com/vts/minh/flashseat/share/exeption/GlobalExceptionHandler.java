package com.vts.minh.flashseat.share.exeption;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleException(DataIntegrityViolationException exception){
        for(Throwable cause = exception; cause != null; cause = cause.getCause()){
            if(cause instanceof ConstraintViolationException violationException && "uk_users_email".equals(violationException.getConstraintName())){
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("Status", 409);
                map.put("message", "Email already exists");
                return ResponseEntity.status(409).body(map);
            }
        }
        throw  exception;
    }
}
