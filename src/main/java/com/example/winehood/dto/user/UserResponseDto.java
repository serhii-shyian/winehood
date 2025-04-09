package com.example.winehood.dto.user;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName) {
}
