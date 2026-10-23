package com.vts.minh.flashseat.user.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record updateUserRequest(

        @NotBlank
        @Size(max = 100)
        String fullName
) {
}
