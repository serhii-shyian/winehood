package com.example.winehood.dto.order;

import com.example.winehood.dto.orderitem.OrderItemDto;
import com.example.winehood.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDto(
        Long id,
        Long userId,
        LocalDateTime orderDate,
        Set<OrderItemDto> orderItems,
        Order.Status status,
        BigDecimal total) {
}
