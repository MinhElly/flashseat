package com.vts.minh.flashseat.user.internal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record createUserRequest (
        @NotBlank
        @Size(max = 100)
        String fullName,
        @NotBlank
        @Email
        @Size(max = 255)
        String email
){}
