package com.example.winehood.controller;

import com.example.winehood.dto.order.CreateOrderRequestDto;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.dto.order.UpdateOrderRequestDto;
import com.example.winehood.dto.orderitem.OrderItemDto;
import com.example.winehood.model.User;
import com.example.winehood.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders management", description = "Endpoint for managing orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit current order",
            description = "Submitting current creating order")
    @PreAuthorize("hasRole('USER')")
    public OrderDto submitOrder(@AuthenticationPrincipal User user,
                                @RequestBody @Valid CreateOrderRequestDto requestDto) {
        return orderService.createOrder(user.getId(), requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user orders",
            description = "Getting all user orders")
    @PreAuthorize("hasRole('USER')")
    public Page<OrderDto> getOrders(@AuthenticationPrincipal User user,
                                    @ParameterObject
                                    @PageableDefault(
                                            size = 5,
                                            sort = "userId",
                                            direction = Sort.Direction.ASC)
                                    Pageable pageable) {
        return orderService.getOrders(user.getId(), pageable);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update order status",
            description = "Updating order status by orderId")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto updateOrderStatus(@PathVariable @Positive Long orderId,
                                      @RequestBody @Valid UpdateOrderRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }

    @GetMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get items from order",
            description = "Getting items from order by orderId")
    @PreAuthorize("hasRole('USER')")
    public Page<OrderItemDto> getOrderItemsByOrderId(@PathVariable @Positive Long orderId,
                                                     @ParameterObject
                                                     @PageableDefault(
                                                             size = 5,
                                                             sort = "wineId",
                                                             direction = Sort.Direction.ASC)
                                                     Pageable pageable) {
        return orderService.getOrderItemsByOrderId(orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{orderItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get order items",
            description = "Getting order items by orderId and ItemId")
    @PreAuthorize("hasRole('USER')")
    public OrderItemDto getOrderItemByIdAndOrderId(@PathVariable @Positive Long orderId,
                                                   @PathVariable @Positive Long orderItemId) {
        return orderService.getOrderItemByIdAndOrderId(orderId, orderItemId);
    }
}
