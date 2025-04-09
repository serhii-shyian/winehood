package com.example.winehood.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record CreateReviewRequestDto(
        @NotNull(message = "Wine id is required")
        @Positive
        Long wineId,
        @NotBlank(message = "Wine description is required")
        @Length(min = 10, max = 255)
        String text,
        @Positive
        Double rating) {
}
