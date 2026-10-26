package com.vts.minh.flashseat.user.mapper;

import com.vts.minh.flashseat.user.dto.response.UserResponse;
import com.vts.minh.flashseat.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
