package com.vts.minh.marketingaiagentteam.user.dto.response;

import com.vts.minh.marketingaiagentteam.user.enums.UserRole;
import com.vts.minh.marketingaiagentteam.user.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
