package com.example.winehood.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(
        @NotNull(message = "Wine quantity may not be null")
        @Positive
        Integer quantity) {
}
