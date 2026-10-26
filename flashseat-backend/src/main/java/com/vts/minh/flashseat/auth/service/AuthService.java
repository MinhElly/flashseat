package com.vts.minh.flashseat.auth.service;

import com.vts.minh.flashseat.auth.dto.request.RegisterRequest;
import com.vts.minh.flashseat.config.SecurityConfig;
import com.vts.minh.flashseat.user.dto.response.UserResponse;
import com.vts.minh.flashseat.user.entity.User;
import com.vts.minh.flashseat.user.mapper.UserMapper;
import com.vts.minh.flashseat.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final SecurityConfig  securityConfig;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, SecurityConfig securityConfig, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
        this.userMapper = userMapper;
    }
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if(registerRequest.password().getBytes(StandardCharsets.UTF_8).length > 72){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must not exceed 72 UTF-8 bytes");
        }
        String email = registerRequest.email().trim().toLowerCase(Locale.ROOT);
        if(userRepository.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User(registerRequest.fullName().trim(), email);
        user.setPasswordHash(securityConfig.passwordEncoder().encode(registerRequest.password()));

        return userMapper.toResponse(userRepository.save(user));
    }
}
