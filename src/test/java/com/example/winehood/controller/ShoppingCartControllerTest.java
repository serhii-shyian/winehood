package com.example.winehood.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.winehood.dto.cartitem.CartItemDto;
import com.example.winehood.dto.cartitem.CreateCartItemRequestDto;
import com.example.winehood.dto.cartitem.UpdateCartItemRequestDto;
import com.example.winehood.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
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
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
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
    @DisplayName("""
            Get ShoppingCart by user id when user exists
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void getUserShoppingCart_ExistingUser_ReturnsShoppingCartDto() throws Exception {
        //Given
        ShoppingCartDto expected = getShoppingCartDto();

        //When
        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse()
                        .getContentAsByteArray(), ShoppingCartDto.class
        );
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    @DisplayName("""
            Add wine to ShoppingCart when wine exists
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void addWineToShoppingCart_ExistingWine_ReturnsCartItemDto() throws Exception {
        //Given
        CreateCartItemRequestDto requestDto = getCreateCartItemRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expected = getCartItemDto();

        //When
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse()
                        .getContentAsByteArray(), CartItemDto.class
        );
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(3)
    @DisplayName("""
            Add wine to ShoppingCart when wine does not exist
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void addWineToShoppingCart_NonExistingWine_ReturnsNotFound() throws Exception {
        // Given
        CreateCartItemRequestDto requestDto = getCreateCartItemRequestDtoWithNonExistingWineId();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("""
            Update the wine quantity in ShoppingCart when given valid quantity
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void updateWineInShoppingCart_ValidParams_ReturnsShoppingCartDto() throws Exception {
        //Given
        UpdateCartItemRequestDto requestDto =
                new UpdateCartItemRequestDto(5);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expected = new CartItemDto(
                1L,
                1L,
                "Wine A",
                5);

        //When
        MvcResult result = mockMvc.perform(
                        put("/cart/items/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        //Then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse()
                        .getContentAsByteArray(), CartItemDto.class
        );
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(5)
    @DisplayName("""
            Update the wine quantity in ShoppingCart when given invalid quantity
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void updateWineInShoppingCart_InvalidQuantity_ReturnsBadRequest() throws Exception {
        // Given
        UpdateCartItemRequestDto requestDto =
                new UpdateCartItemRequestDto(-3);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/cart/items/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("""
            Delete wine from ShoppingCart when wine exists
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void deleteWineFromShoppingCart_ExistingCartItemId_ReturnsNothing() throws Exception {
        //When
        MvcResult result = mockMvc.perform(
                        delete("/cart/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        //Then
        String actual = result.getResponse().getContentAsString();
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("""
            Delete wine from ShoppingCart when wine does not exist
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void deleteWineFromShoppingCart_NonExistingCartItemId_ReturnsNotFound() throws Exception {
        //When
        mockMvc.perform(
                        delete("/cart/items/999")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNotFound());
    }

    private ShoppingCartDto getShoppingCartDto() {
        return new ShoppingCartDto()
                .setId(4L)
                .setUserId(4L)
                .setCartItems(Set.of(getCartItemDto()));
    }

    private CartItemDto getCartItemDto() {
        return new CartItemDto(
                1L,
                1L,
                "Wine A",
                6);
    }

    private CreateCartItemRequestDto getCreateCartItemRequestDto() {
        return new CreateCartItemRequestDto(
                1L,
                6);
    }

    private CreateCartItemRequestDto getCreateCartItemRequestDtoWithNonExistingWineId() {
        return new CreateCartItemRequestDto(
                99L,
                1);
    }
}
