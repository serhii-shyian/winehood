package com.example.winehood.dto.cartitem;

public record CartItemDto(
        Long id,
        Long wineId,
        String wineName,
        Integer quantity) {
}
