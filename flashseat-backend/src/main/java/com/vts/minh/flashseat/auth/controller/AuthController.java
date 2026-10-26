package com.vts.minh.flashseat.auth.controller;

import com.vts.minh.flashseat.auth.dto.request.RegisterRequest;
import com.vts.minh.flashseat.auth.service.AuthService;
import com.vts.minh.flashseat.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse user = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
