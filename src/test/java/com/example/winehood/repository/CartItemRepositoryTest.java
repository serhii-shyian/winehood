package com.example.winehood.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.winehood.model.CartItem;
import com.example.winehood.model.Region;
import com.example.winehood.model.ShoppingCart;
import com.example.winehood.model.User;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.cartitem.CartItemRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

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
            Find cart items list by shopping cart id when shopping cart id exists
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
    void findCartItemsListByShoppingCartId_ExistedShoppingCartId_ReturnsCartItemsList() {
        // Given
        Wine wine = getWine();
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        CartItem cartItem = getCartItem(shoppingCart, wine);
        List<CartItem> expected = List.of(cartItem);

        // When
        List<CartItem> actual = cartItemRepository
                .findListByShoppingCartId(shoppingCart.getId(), Pageable.ofSize(5));

        // Then
        assertNotNull(actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("wine", "shoppingCart")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Find cart items list by non-existent shopping cart id returns empty list
            """)
    void findCartItemsListByShoppingCartId_NonExistentShoppingCartId_ReturnsEmptyList() {
        // When
        List<CartItem> actual = cartItemRepository
                .findListByShoppingCartId(999L, Pageable.ofSize(5));

        // Then
        assertNotNull(actual);
        assertThat(actual).isEmpty();
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
                .setId(wine.getId())
                .setShoppingCart(shoppingCart)
                .setWine(wine)
                .setQuantity(6);
    }
}
