package com.example.winehood.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemRequestDto(
        @NotNull(message = "Wine id may not be null")
        @Positive
        Long wineId,
        @NotNull(message = "Wine quantity may not be null")
        @Positive
        Integer quantity) {
}
