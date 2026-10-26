package com.vts.minh.flashseat.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @NotBlank
        @Size(max = 100)
        String fullName
) {
}
