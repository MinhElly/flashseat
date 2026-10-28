package com.vts.minh.marketingaiagentteam.auth.controller;

import com.vts.minh.marketingaiagentteam.auth.dto.request.LoginRequest;
import com.vts.minh.marketingaiagentteam.auth.dto.request.RegisterRequest;
import com.vts.minh.marketingaiagentteam.auth.dto.response.LoginResponse;
import com.vts.minh.marketingaiagentteam.auth.service.AuthService;
import com.vts.minh.marketingaiagentteam.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(Map.of(
                "userId", jwt.getSubject(),
                "role", jwt.getClaimAsString("role")
        ));
    }
}
