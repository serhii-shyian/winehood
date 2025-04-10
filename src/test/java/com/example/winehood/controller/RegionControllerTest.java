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

import com.example.winehood.dto.region.CreateRegionRequestDto;
import com.example.winehood.dto.region.RegionDto;
import com.example.winehood.dto.wine.WineDtoWithoutRegion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class RegionControllerTest {
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
    @DisplayName("Get list of all regions when they exist")
    @WithMockUser(username = "user")
    void getAllRegions_RegionsExist_ReturnsRegionDtoPage() throws Exception {
        // Given
        List<RegionDto> regionDtoList = getRegionDtoList();
        int expectedTotalElements = regionDtoList.size();

        // When
        MvcResult result = mockMvc.perform(
                        get("/regions")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sort", "id")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode contentNode = root.path("content");
        List<RegionDto> actualList = Arrays.asList(
                objectMapper.treeToValue(contentNode, RegionDto[].class));

        int actualTotalElements = root.path("totalElements").asInt();
        assertEquals(expectedTotalElements, actualTotalElements);
        assertEquals(regionDtoList, actualList);
    }

    @Test
    @Order(2)
    @DisplayName("Get region by id when it exists")
    @WithMockUser(username = "user")
    void getRegionById_ExistingId_ReturnsRegionDto() throws Exception {
        // Given
        RegionDto expected = getRegionDtoList().getFirst();

        // When
        MvcResult result = mockMvc.perform(
                        get("/regions/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        RegionDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RegionDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(3)
    @DisplayName("Get wines by region id when region exists")
    @WithMockUser(username = "user")
    void getWinesByRegionId_ExistingRegion_ReturnsWineDtoPage() throws Exception {
        // Given
        int regionId = 1; // Assuming region with ID 1 exists and is associated with wines
        List<WineDtoWithoutRegion> expectedWines = getExpectedWinesForRegion(regionId);
        int expectedTotalElements = expectedWines.size();

        // When
        MvcResult result = mockMvc.perform(
                        get("/regions/" + regionId + "/wines")
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
        List<WineDtoWithoutRegion> actualWines = Arrays.asList(
                objectMapper.treeToValue(contentNode, WineDtoWithoutRegion[].class));

        int actualTotalElements = root.path("totalElements").asInt();
        assertEquals(expectedTotalElements, actualTotalElements);
        assertEquals(expectedWines, actualWines);
    }

    private List<WineDtoWithoutRegion> getExpectedWinesForRegion(int regionId) {
        return List.of(
                new WineDtoWithoutRegion("Wine A", BigDecimal.valueOf(20.0), "Merlot"),
                new WineDtoWithoutRegion("Wine B", BigDecimal.valueOf(30.0), "Cabernet Sauvignon")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Create a new region from valid DTO")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createRegion_ValidRequest_ReturnsRegionDto() throws Exception {
        // Given
        CreateRegionRequestDto requestDto = getCreateRegionRequestDtoList().getFirst();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        RegionDto expected = getRegionDtoFromRequestDto(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/regions")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        RegionDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RegionDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @Order(5)
    @DisplayName("Update region by id when it exists")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateRegion_ExistingId_ReturnsRegionDto() throws Exception {
        // Given
        CreateRegionRequestDto requestDto = getCreateRegionRequestDtoList().get(1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        RegionDto expected = getRegionDtoFromRequestDto(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/regions/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        RegionDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RegionDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(6)
    @DisplayName("Delete region by id when it exists")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteRegionById_ExistingId_DeletesSuccessfully() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        delete("/regions/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        String actual = result.getResponse().getContentAsString();
        assertTrue(actual.isEmpty());
    }

    private List<RegionDto> getRegionDtoList() {
        return List.of(
                new RegionDto(1L, "Napa Valley", "USA"),
                new RegionDto(2L, "Tuscany", "Italy"),
                new RegionDto(3L, "Bordeaux", "France")
        );
    }

    private List<CreateRegionRequestDto> getCreateRegionRequestDtoList() {
        return List.of(
                new CreateRegionRequestDto("Napa Valley", "USA"),
                new CreateRegionRequestDto("Tuscany", "Italy")
        );
    }

    private RegionDto getRegionDtoFromRequestDto(CreateRegionRequestDto requestDto) {
        return new RegionDto(1L, requestDto.name(), requestDto.country());
    }
}
