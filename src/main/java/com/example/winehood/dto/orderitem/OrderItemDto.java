package com.example.winehood.dto.orderitem;

public record OrderItemDto(
        Long id,
        Long wineId,
        Integer quantity) {
}
