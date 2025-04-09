package com.example.winehood.dto.order;

import com.example.winehood.model.Order;

public record UpdateOrderRequestDto(
        Order.Status status) {
}
