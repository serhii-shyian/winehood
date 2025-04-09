package com.example.winehood.dto.user;

import com.example.winehood.validation.Password;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank(message = "Username may not be blank")
        String username,
        @NotBlank(message = "Password may not be blank")
        @Password
        String password) {
}
