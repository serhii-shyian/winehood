package com.example.winehood.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.winehood.repository.review.ReviewRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

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
            Find reviews by wineId when wine exists
            """)
    @Sql(scripts = {
            "classpath:database/roles/insert-into-roles.sql",
            "classpath:database/users/insert-into-users.sql",
            "classpath:database/regions/insert-into-regions.sql",
            "classpath:database/wines/insert-into-wines.sql",
            "classpath:database/reviews/insert-into-reviews.sql" },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/reviews/delete-all-from-reviews.sql",
            "classpath:database/wines/delete-all-from-wines.sql",
            "classpath:database/regions/delete-all-from-regions.sql",
            "classpath:database/users/delete-all-from-users.sql",
            "classpath:database/roles/delete-all-from-roles.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByWineId_ExistingWineId_ReturnsReviewPage() {
        // Given
        Long wineId = 1L;
        int expectedTotalElements = 2;

        // When
        var reviewPage = reviewRepository.findAllByWineId(wineId, Pageable.ofSize(5));

        // Then
        assertNotNull(reviewPage);
        assertEquals(expectedTotalElements, reviewPage.getTotalElements());
        assertEquals(2, reviewPage.getContent().size());
        assertTrue(reviewPage.getContent().stream()
                .allMatch(review -> review.getWine().getId().equals(wineId)));
    }

    @Test
    @DisplayName("""
            Find reviews by wineId when no reviews exist
            """)
    @Sql(scripts = {
            "classpath:database/regions/insert-into-regions.sql",
            "classpath:database/wines/insert-into-wines.sql" },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/wines/delete-all-from-wines.sql",
            "classpath:database/regions/delete-all-from-regions.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByWineId_NoReviewsForWine_ReturnsEmptyPage() {
        // Given
        Long wineId = 99L;

        // When
        var reviewPage = reviewRepository.findAllByWineId(wineId, Pageable.ofSize(5));

        // Then
        assertNotNull(reviewPage);
        assertEquals(0, reviewPage.getTotalElements());
    }
}
