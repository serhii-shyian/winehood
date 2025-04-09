package com.example.winehood.dto.review;

import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        Long wineId,
        Long userId,
        Double rating,
        String text,
        LocalDateTime timestamp) {
}
