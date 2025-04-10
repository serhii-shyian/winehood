package com.example.winehood.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.winehood.dto.wine.CreateWineRequestDto;
import com.example.winehood.dto.wine.WineDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WineControllerTest {
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
                    new ClassPathResource("database/regions/insert-into-regions.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/wines/insert-into-wines.sql"));
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
                    new ClassPathResource("database/wines/delete-all-from-wines.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/regions/delete-all-from-regions.sql"));
        }
    }

    @Test
    @Order(1)
    @DisplayName("""
                Get list of all wines when they exist
                """)
    @WithMockUser(username = "user")
    void getAllWines_WinesExist_ReturnsWineDtoPage()
            throws Exception {
        // Given
        List<WineDto> wineDtoList = getWineDtoList();
        int expectedTotalElements = wineDtoList.size();

        // When
        MvcResult result = mockMvc.perform(
                        get("/wines")
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode contentNode = root.path("content");
        List<WineDto> actualList = Arrays.asList(
                objectMapper.treeToValue(contentNode, WineDto[].class));

        int actualTotalElements = root.path("totalElements").asInt();
        assertEquals(expectedTotalElements, actualTotalElements);
        assertEquals(wineDtoList, actualList);
    }

    @Test
    @Order(2)
    @DisplayName("""
                Get wine by id when it exists
                """)
    @WithMockUser(username = "user")
    void getById_ExistingWineId_ReturnsWineDto()
            throws Exception {
        // Given
        WineDto expected = getWineDtoList().getFirst();
        expected.setPrice(expected.getPrice()
                .setScale(2, RoundingMode.HALF_UP));

        // When
        MvcResult result = mockMvc.perform(
                        get("/wines/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        WineDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), WineDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(3)
    @DisplayName("""
                Create a new wine from valid DTO
                """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createWine_ValidRequestDto_ReturnsWineDto()
            throws Exception {
        // Given
        CreateWineRequestDto requestDto
                = getCreateWineRequestDtoList().get(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        WineDto expected = getWineDtoFromRequestDto(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/wines")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        WineDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), WineDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @Order(4)
    @DisplayName("""
                Update wine by id when it exists
                """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateWine_ExistingWineId_ReturnsWineDto()
            throws Exception {
        // Given
        CreateWineRequestDto requestDto
                = getCreateWineRequestDtoList().get(1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        WineDto expected = getWineDtoFromRequestDto(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/wines/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        WineDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), WineDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(5)
    @DisplayName("""
                Delete wine by id when it exists
                """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteWine_ExistingWineId_ReturnsNothing()
            throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        delete("/wines/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        String actual = result.getResponse().getContentAsString();
        assertTrue(actual.isEmpty());
    }

    private List<WineDto> getWineDtoList() {
        return List.of(
                new WineDto()
                        .setId(1L)
                        .setName("Wine A")
                        .setPrice(BigDecimal.valueOf(20.00))
                        .setGrapeVariety("Merlot")
                        .setRegionId(1L),
                new WineDto()
                        .setId(2L)
                        .setName("Wine B")
                        .setPrice(BigDecimal.valueOf(30.00))
                        .setGrapeVariety("Cabernet Sauvignon")
                        .setRegionId(1L),
                new WineDto()
                        .setId(3L)
                        .setName("Wine C")
                        .setPrice(BigDecimal.valueOf(25.00))
                        .setGrapeVariety("Pinot Noir")
                        .setRegionId(2L)
        );
    }

    private List<CreateWineRequestDto> getCreateWineRequestDtoList() {
        return List.of(
                new CreateWineRequestDto(
                        "Sauvignon Blanc",
                        BigDecimal.valueOf(22.00),
                        "Sauvignon Blanc",
                        3L),
                new CreateWineRequestDto(
                        "Cabernet Sauvignon",
                        BigDecimal.valueOf(30.00),
                        "Cabernet Sauvignon",
                        1L)
        );
    }

    private WineDto getWineDtoFromRequestDto(CreateWineRequestDto requestDto) {
        return new WineDto()
                .setId(1L)
                .setName(requestDto.name())
                .setPrice(requestDto.price())
                .setGrapeVariety(requestDto.grapeVariety())
                .setRegionId(requestDto.regionId());
    }
}
