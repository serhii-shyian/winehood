package com.example.winehood.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewControllerTest {
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
                    new ClassPathResource("database/reviews/insert-into-reviews.sql"));
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
                    new ClassPathResource("database/reviews/delete-all-from-reviews.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/wines/delete-all-from-wines.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/regions/delete-all-from-regions.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/delete-all-from-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/delete-all-from-roles.sql"));
        }
    }

    @Test
    @Order(1)
    @DisplayName("""
            Get all reviews by wine id
            """)
    @WithMockUser(username = "john.doe")
    void getAllReviewsByWineId_ExistingWineId_ReturnsReviewDtoPage()
            throws Exception {
        // Given
        long wineId = 1L;
        int expectedTotalElements = 2;

        // When
        MvcResult result = mockMvc.perform(
                        get("/reviews")
                                .param("wineId", Long.toString(wineId))
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode contentNode = root.path("content");
        List<ReviewDto> actualList = Arrays.asList(
                objectMapper.treeToValue(contentNode, ReviewDto[].class));

        int actualTotalElements = root.path("totalElements").asInt();
        assertEquals(expectedTotalElements, actualTotalElements);
        assertNotNull(actualList);
    }

    @Test
    @Order(2)
    @DisplayName("""
            Get reviews for non-existent wine ID returns empty list
            """)
    @WithMockUser(username = "john.doe")
    void getReviewsForNonExistentWineId_ReturnsEmptyList()
            throws Exception {
        // Given
        long nonExistentWineId = 999L;

        // When
        MvcResult result = mockMvc.perform(
                        get("/reviews")
                                .param("wineId", Long.toString(nonExistentWineId))
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode contentNode = root.path("content");
        assertTrue(contentNode.isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("""
            Create a new review from valid DTO
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void createReview_ValidRequestDto_ReturnsReviewDto()
            throws Exception {
        // Given
        CreateReviewRequestDto requestDto = getCreateReviewRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        ReviewDto expectedDto = getReviewDto();

        // When
        MvcResult result = mockMvc.perform(
                        post("/reviews")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        ReviewDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ReviewDto.class);
        assertNotNull(actual);
        assertEquals(expectedDto.text(), actual.text());
        assertEquals(expectedDto.rating(), actual.rating());
    }

    @Test
    @Order(4)
    @DisplayName("""
            Create review with invalid wine ID throws EntityNotFoundException
            """)
    @WithUserDetails(value = "john.doe",
            userDetailsServiceBeanName = "customUserDetailsService")
    void createReview_InvalidWineId_ThrowsEntityNotFoundException()
            throws Exception {
        // Given
        CreateReviewRequestDto requestDto = new CreateReviewRequestDto(
                99L,
                "Great wine with smooth finish",
                4.5
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When & Then
        mockMvc.perform(
                        post("/reviews")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    private CreateReviewRequestDto getCreateReviewRequestDto() {
        return new CreateReviewRequestDto(
                1L,
                "Great wine with smooth finish",
                4.5);
    }

    private ReviewDto getReviewDto() {
        return new ReviewDto(
                1L,
                1L,
                4L,
                4.5,
                "Great wine with smooth finish",
                LocalDateTime.now());
    }
}
