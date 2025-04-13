package com.example.winehood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.winehood.dto.order.CreateOrderRequestDto;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.dto.order.UpdateOrderRequestDto;
import com.example.winehood.dto.orderitem.OrderItemDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.OrderItemMapper;
import com.example.winehood.mapper.OrderMapper;
import com.example.winehood.model.Order;
import com.example.winehood.model.OrderItem;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.order.OrderRepository;
import com.example.winehood.repository.orderitem.OrderItemRepository;
import com.example.winehood.repository.shoppingcart.ShoppingCartRepository;
import com.example.winehood.service.order.OrderServiceImpl;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("""
            Create Order when request is invalid
            """)
    void createOrder_InvalidRequest_ThrowsException() {
        //Given
        User user = getTestUser();
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(LocalDateTime.now(), "");

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> orderService.createOrder(user.getId(), requestDto));
    }

    @Test
    @DisplayName("""
            Get Orders when user has orders
            """)
    void getOrders_UserWithOrders_ReturnsOrderDtoList() {
        //Given
        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-04-12T12:30:00Z"),
                ZoneId.of("UTC"));
        User user = getTestUser();
        List<Order> orders = List.of(getOrder(user, fixedClock));
        Pageable pageable = PageRequest.of(0, 5);
        Page<Order> orderPage = new PageImpl<>(orders);
        List<OrderDto> expected = List.of(getOrderDto(orders.getFirst()));

        when(orderRepository.findAllByUserId(user.getId(), pageable)).thenReturn(orderPage);
        when(orderMapper.toDto(orders.getFirst())).thenReturn(expected.getFirst());

        //When
        Page<OrderDto> actual = orderService.getOrders(user.getId(), pageable);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual.getContent());
        verify(orderRepository, times(1))
                .findAllByUserId(user.getId(), pageable);
        verify(orderMapper, times(1)).toDto(orders.getFirst());
    }

    @Test
    @DisplayName("""
            Get Orders when user has no orders
            """)
    void getOrders_UserWithoutOrders_ReturnsEmptyList() {
        //Given
        User user = getTestUser();
        Pageable pageable = PageRequest.of(0, 5);

        when(orderRepository.findAllByUserId(user.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of()));

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> orderService.getOrders(user.getId(), pageable));
        verify(orderRepository, times(1))
                .findAllByUserId(user.getId(), pageable);
    }

    @Test
    @DisplayName("""
            Update Order Status when given valid order id
            """)
    void updateOrderStatus_ValidOrderId_ReturnsOrderDto() {
        //Given
        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-04-12T12:30:00Z"),
                ZoneId.of("UTC"));
        User user = getTestUser();
        Order order = getOrder(user, fixedClock);
        UpdateOrderRequestDto requestDto = new UpdateOrderRequestDto(Order.Status.SHIPPING);
        OrderDto expected = getOrderDto(order);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);

        //When
        OrderDto actual = orderService.updateOrderStatus(order.getId(), requestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDto(order);
    }

    @Test
    @DisplayName("""
            Update Order Status when order does not exist
            """)
    void updateOrderStatus_NonExistingOrder_ThrowsException() {
        //Given
        UpdateOrderRequestDto requestDto = new UpdateOrderRequestDto(Order.Status.SHIPPING);

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(99L, requestDto));
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("""
            Get Order Items by Order Id when order exists
            """)
    void getOrderItemsByOrderId_ExistingOrder_ReturnsOrderItemList() {
        //Given
        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-04-12T12:30:00Z"),
                ZoneId.of("UTC"));
        User user = getTestUser();
        Order order = getOrder(user, fixedClock);
        List<OrderItem> orderItems = List.of(getOrderItem(order));
        List<OrderItemDto> expected = List.of(getOrderItemDto(orderItems.getFirst()));

        when(orderItemRepository.findAllByOrderId(order.getId(), PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(orderItems));
        when(orderItemMapper.toDto(orderItems.getFirst())).thenReturn(expected.getFirst());

        //When
        Page<OrderItemDto> actual = orderService.getOrderItemsByOrderId(
                order.getId(), PageRequest.of(0, 5));

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual.getContent());
        verify(orderItemRepository, times(1)).findAllByOrderId(
                order.getId(), PageRequest.of(0, 5));
        verify(orderItemMapper, times(1)).toDto(orderItems.getFirst());
    }

    @Test
    @DisplayName("""
            Get Order Item by Id and Order Id when order item exists
            """)
    void getOrderItemByIdAndOrderId_ExistingOrderItem_ReturnsOrderItemDto() {
        //Given
        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-04-12T12:30:00Z"),
                ZoneId.of("UTC"));
        User user = getTestUser();
        Order order = getOrder(user, fixedClock).setId(1L);
        OrderItem orderItem = getOrderItem(order);
        OrderItemDto expected = getOrderItemDto(orderItem);

        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(orderItem)).thenReturn(expected);

        //When
        OrderItemDto actual = orderService.getOrderItemByIdAndOrderId(
                orderItem.getId(), order.getId());

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(orderItemRepository, times(1)).findById(orderItem.getId());
        verify(orderItemMapper, times(1)).toDto(orderItem);
    }

    @Test
    @DisplayName("""
            Get Order Item by Id and Order Id when order item does not exist
            """)
    void getOrderItemByIdAndOrderId_NonExistingOrderItem_ThrowsException() {
        //Given
        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> orderService.getOrderItemByIdAndOrderId(99L, 99L));
        verify(orderItemRepository, times(1)).findById(99L);
    }

    private User getTestUser() {
        return new User()
                .setId(4L)
                .setUsername("john.doe")
                .setEmail("john.doe@example.com")
                .setPassword("password123")
                .setFirstName("John")
                .setLastName("Doe")
                .setShippingAddress("Ukraine");
    }

    private static OrderItemDto getOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getWine().getId(),
                orderItem.getQuantity());
    }

    private Order getOrder(User user, Clock clock) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now(clock));
        order.setShippingAddress("123 Main Street, Cityville");
        order.setTotal(BigDecimal.valueOf(420.0));
        order.setOrderItems(new HashSet<>());
        return order;
    }

    private OrderItem getOrderItem(Order order) {
        return new OrderItem()
                .setId(1L)
                .setOrder(order)
                .setWine(new Wine().setId(1L))
                .setQuantity(3);
    }

    private OrderDto getOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getOrderDate(),
                Set.of(new OrderItemDto(1L, 1L, 3)),
                order.getStatus(),
                order.getTotal(),
                order.getShippingAddress()
        );
    }
}
