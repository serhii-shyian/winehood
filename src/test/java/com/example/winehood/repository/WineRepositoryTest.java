package com.example.winehood.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.winehood.repository.wine.WineRepository;
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
public class WineRepositoryTest {

    @Autowired
    private WineRepository wineRepository;

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
            Find wines by regionId when region exists
            """)
    @Sql(scripts = {
            "classpath:database/regions/insert-into-regions.sql",
            "classpath:database/wines/insert-into-wines.sql" },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/wines/delete-all-from-wines.sql",
            "classpath:database/regions/delete-all-from-regions.sql" },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByRegionId_ExistingRegionId_ReturnsWinePage() {
        // Given
        Long regionId = 1L;
        int expectedTotalElements = 2;

        // When
        var winePage = wineRepository.findAllByRegionId(regionId, Pageable.ofSize(5));

        // Then
        assertNotNull(winePage);
        assertEquals(expectedTotalElements, winePage.getTotalElements());
        assertEquals(2, winePage.getContent().size());
        assertTrue(winePage.getContent().stream()
                .allMatch(wine -> wine.getRegion().getId().equals(regionId)));
    }

    @Test
    @DisplayName("""
            Find wines by regionId when no wines exist
            """)
    @Sql(scripts = {
            "classpath:database/regions/insert-into-regions.sql" },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = { "classpath:database/regions/delete-all-from-regions.sql" },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByRegionId_NoWinesForRegion_ReturnsEmptyPage() {
        // Given
        Long regionId = 99L;

        // When
        var winePage = wineRepository.findAllByRegionId(regionId, Pageable.ofSize(5));

        // Then
        assertNotNull(winePage);
        assertEquals(0, winePage.getTotalElements());
    }
}
