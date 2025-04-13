package com.example.winehood.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.winehood.dto.order.CreateOrderRequestDto;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.dto.order.UpdateOrderRequestDto;
import com.example.winehood.dto.orderitem.OrderItemDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/insert-into-roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/insert-into-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/regions/insert-into-regions.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/wines/insert-into-wines.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcarts/insert-into-shopping_carts.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/cartitems/insert-into-cart_items.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/orders/insert-into-orders.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/orderitems/insert-into-order_items.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/orderitems/delete-all-from-order_items.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/orders/delete-all-from-orders.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/cartitems/delete-all-from-cart_items.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/shoppingcarts/delete-all-from-shopping_carts.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/wines/delete-all-from-wines.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/regions/delete-all-from-regions.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/users/delete-all-from-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/roles/delete-all-from-roles.sql"));
        }
    }

    @Test
    @Order(1)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("Submit order when data is valid")
    void submitOrder_ValidRequest_ReturnsOrderDto() throws Exception {
        // Given
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(
                LocalDateTime.of(2025, 4, 12, 14, 0, 0),
                "123 Main Street, Cityville"
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderDto.class);
        assertNotNull(actual);
        assertEquals("123 Main Street, Cityville", actual.shippingAddress());
    }

    @Test
    @Order(2)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("Get all orders for user")
    void getOrders_ExistingUser_ReturnsOrderPage() throws Exception {
        // Given
        MvcResult result = mockMvc.perform(
                get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // When
        JsonNode rootNode = objectMapper.readTree(
                result.getResponse().getContentAsByteArray());
        JsonNode contentNode = rootNode.get("content");
        List<OrderDto> orders = objectMapper.readValue(contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, OrderDto.class));

        // Then
        assertFalse(orders.isEmpty());
    }

    @Test
    @Order(3)
    @WithUserDetails(value = "admin",
            userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("Update order status")
    void updateOrderStatus_ValidRequest_ReturnsUpdatedOrderDto() throws Exception {
        // Given
        UpdateOrderRequestDto requestDto = new UpdateOrderRequestDto(
                com.example.winehood.model.Order.Status.SHIPPING);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        OrderDto updatedOrder = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderDto.class);
        assertEquals(com.example.winehood.model.Order.Status.SHIPPING,
                updatedOrder.status());
    }

    @Test
    @Order(4)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("Get items for specific order")
    void getOrderItems_ExistingOrderId_ReturnsOrderItemPage() throws Exception {
        // Given
        MvcResult result = mockMvc.perform(
                get("/orders/1/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // When
        JsonNode rootNode = objectMapper.readTree(result.getResponse().getContentAsByteArray());
        JsonNode contentNode = rootNode.get("content");

        List<OrderItemDto> orderItems = objectMapper.readValue(
                contentNode.toString(), objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, OrderItemDto.class));

        // Then
        assertFalse(orderItems.isEmpty());
    }

    @Test
    @Order(5)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("Get specific order item")
    void getOrderItem_ExistingOrderItemId_ReturnsOrderItemDto() throws Exception {
        // Given
        MvcResult result = mockMvc.perform(
                get("/orders/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // When
        OrderItemDto orderItem = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderItemDto.class);

        // Then
        assertNotNull(orderItem);
    }
}
