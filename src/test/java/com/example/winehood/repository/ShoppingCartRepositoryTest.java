package com.example.winehood.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.model.CartItem;
import com.example.winehood.model.Region;
import com.example.winehood.model.ShoppingCart;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.shoppingcart.ShoppingCartRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-all-data-before-tests.sql"));
        }
    }

    @Test
    @DisplayName("""
            Find shopping cart by user id when user exists
            """)
    @Sql(scripts = {
            "classpath:database/roles/insert-into-roles.sql",
            "classpath:database/users/insert-into-users.sql",
            "classpath:database/regions/insert-into-regions.sql",
            "classpath:database/wines/insert-into-wines.sql",
            "classpath:database/shoppingcarts/insert-into-shopping_carts.sql",
            "classpath:database/cartitems/insert-into-cart_items.sql",},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/cartitems/delete-all-from-cart_items.sql",
            "classpath:database/shoppingcarts/delete-all-from-shopping_carts.sql",
            "classpath:database/wines/delete-all-from-wines.sql",
            "classpath:database/regions/delete-all-from-regions.sql",
            "classpath:database/users/delete-all-from-users.sql",
            "classpath:database/roles/delete-all-from-roles.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findByUserId_GivenValidUserId_ShouldReturnShoppingCart() {
        // Given
        Wine wine = getWine();
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        CartItem cartItem = getCartItem(shoppingCart, wine);
        ShoppingCart expected = shoppingCart.setCartItems(Set.of(cartItem));

        // When
        ShoppingCart actual = shoppingCartRepository.findByUserId(4L)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a shopping cart by userId " + 4L));

        // Then
        assertNotNull(actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("user", "cartItems")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Find shopping cart by non-existent user id should return empty optional
            """)
    void findByUserId_GivenNonExistentUserId_ShouldReturnEmptyOptional() {
        // When
        boolean isPresent = shoppingCartRepository.findByUserId(999L).isPresent();

        // Then
        assertThat(isPresent).isFalse();
    }

    private Wine getWine() {
        return new Wine()
                .setId(1L)
                .setName("Wine A")
                .setPrice(BigDecimal.valueOf(20.0))
                .setGrapeVariety("Merlot")
                .setRegion(getTestRegion());
    }

    private Region getTestRegion() {
        return new Region()
                .setId(1L)
                .setName("Napa Valley")
                .setCountry("USA");
    }

    private User getUser() {
        return new User()
                .setId(4L)
                .setUsername("john.doe")
                .setEmail("john.doe@example.com")
                .setPassword("password123")
                .setFirstName("John")
                .setLastName("Doe")
                .setShippingAddress("Ukraine");
    }

    private ShoppingCart getShoppingCart(User user) {
        return new ShoppingCart()
                .setId(user.getId())
                .setUser(user);
    }

    private CartItem getCartItem(ShoppingCart shoppingCart, Wine wine) {
        return new CartItem()
                .setId(1L)
                .setShoppingCart(shoppingCart)
                .setWine(wine)
                .setQuantity(6);
    }
}
