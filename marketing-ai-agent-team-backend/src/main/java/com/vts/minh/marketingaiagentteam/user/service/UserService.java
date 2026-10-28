package com.vts.minh.marketingaiagentteam.user.service;

import com.vts.minh.marketingaiagentteam.user.dto.request.CreateUserRequest;
import com.vts.minh.marketingaiagentteam.user.dto.request.UpdateUserRequest;
import com.vts.minh.marketingaiagentteam.user.dto.response.UserResponse;
import com.vts.minh.marketingaiagentteam.user.entity.User;
import com.vts.minh.marketingaiagentteam.user.enums.UserStatus;
import com.vts.minh.marketingaiagentteam.user.mapper.UserMapper;
import com.vts.minh.marketingaiagentteam.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User(request.fullName().trim(), email);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse getUserDetail(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUser() {
        return userRepository.findAllByStatusNot(UserStatus.DISABLED).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUser(id);
        user.setFullName(request.fullName().trim());
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        user.disable();
    }

    private User findUser(Long id) {
        return userRepository.findByIdAndStatusNot(id, UserStatus.DISABLED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
