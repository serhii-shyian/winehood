package com.example.winehood.service.order;

import com.example.winehood.dto.order.CreateOrderRequestDto;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.dto.order.UpdateOrderRequestDto;
import com.example.winehood.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(Long userId, CreateOrderRequestDto createOrderDto);

    Page<OrderDto> getOrders(Long userId, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderRequestDto updateOrderDto);

    Page<OrderItemDto> getOrderItemsByOrderId(Long orderId, Pageable pageable);

    OrderItemDto getOrderItemByIdAndOrderId(Long orderId, Long orderItemId);
}
