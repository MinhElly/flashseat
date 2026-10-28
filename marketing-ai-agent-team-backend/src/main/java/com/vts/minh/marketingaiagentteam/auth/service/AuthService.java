package com.vts.minh.marketingaiagentteam.auth.service;

import com.vts.minh.marketingaiagentteam.auth.dto.request.LoginRequest;
import com.vts.minh.marketingaiagentteam.auth.dto.request.RegisterRequest;
import com.vts.minh.marketingaiagentteam.auth.dto.response.LoginResponse;
import com.vts.minh.marketingaiagentteam.config.SecurityConfig;
import com.vts.minh.marketingaiagentteam.user.dto.response.UserResponse;
import com.vts.minh.marketingaiagentteam.user.entity.User;
import com.vts.minh.marketingaiagentteam.user.enums.UserStatus;
import com.vts.minh.marketingaiagentteam.user.mapper.UserMapper;
import com.vts.minh.marketingaiagentteam.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));

        return userMapper.toResponse(userRepository.save(user));
    }
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email().trim().toLowerCase(Locale.ROOT);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginRequest.password()));
        }catch (AuthenticationException ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username and password");
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username and password"));

        if(user.getStatus() != UserStatus.ACTIVE){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username and password");
        }
        String token = jwtService.generateAccessToken(user);

        return new LoginResponse(token, "Bearer", jwtService.getAccessTokenExpiresInSeconds());
    }
}
