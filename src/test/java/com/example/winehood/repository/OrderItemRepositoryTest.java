package com.example.winehood.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.winehood.model.OrderItem;
import com.example.winehood.repository.orderitem.OrderItemRepository;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

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
            Find order items by existing order id
            """)
    @Sql(scripts = {
            "classpath:database/roles/insert-into-roles.sql",
            "classpath:database/users/insert-into-users.sql",
            "classpath:database/regions/insert-into-regions.sql",
            "classpath:database/wines/insert-into-wines.sql",
            "classpath:database/orders/insert-into-orders.sql",
            "classpath:database/orderitems/insert-into-order_items.sql",},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/orderitems/delete-all-from-order_items.sql",
            "classpath:database/orders/delete-all-from-orders.sql",
            "classpath:database/wines/delete-all-from-wines.sql",
            "classpath:database/regions/delete-all-from-regions.sql",
            "classpath:database/users/delete-all-from-users.sql",
            "classpath:database/roles/delete-all-from-roles.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByOrderId_ExistedOrderId_ReturnsOrderItemsPage() {
        // Given
        Long existingOrderId = 1L;

        // When
        Page<OrderItem> actual = orderItemRepository
                .findAllByOrderId(existingOrderId, Pageable.ofSize(5));

        // Then
        assertNotNull(actual);
        assertThat(actual.getContent()).hasSize(2);
        assertThat(actual.getContent())
                .extracting(OrderItem::getId)
                .containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    @DisplayName("""
            Find order items by non-existent order id returns empty page
            """)
    void findAllByOrderId_NonExistentOrderId_ReturnsEmptyPage() {
        // When
        Page<OrderItem> actual = orderItemRepository
                .findAllByOrderId(999L, Pageable.ofSize(5));

        // Then
        assertNotNull(actual);
        assertThat(actual.getContent()).isEmpty();
    }
}
