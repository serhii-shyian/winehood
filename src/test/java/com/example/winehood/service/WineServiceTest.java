package com.example.winehood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.winehood.dto.wine.CreateWineRequestDto;
import com.example.winehood.dto.wine.WineDto;
import com.example.winehood.dto.wine.WineDtoWithoutRegion;
import com.example.winehood.mapper.WineMapper;
import com.example.winehood.model.Region;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.region.RegionRepository;
import com.example.winehood.repository.wine.WineRepository;
import com.example.winehood.service.wine.WineServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class WineServiceTest {

    @InjectMocks
    private WineServiceImpl wineService;

    @Mock
    private WineRepository wineRepository;

    @Mock
    private WineMapper wineMapper;

    @Mock
    private RegionRepository regionRepository;

    @Test
    @DisplayName("""
            Save a new wine and return the corresponding wine DTO
            """)
    void saveWine_ValidRequest_ReturnsWineDto() {
        // Given
        Wine wine = getWine();
        WineDto expectedWineDto = getWineDto();
        CreateWineRequestDto requestDto = getCreateWineRequestDto();

        when(wineMapper.toEntity(requestDto)).thenReturn(wine);
        when(regionRepository.findById(wine.getRegion().getId()))
                .thenReturn(Optional.of(wine.getRegion()));
        when(wineRepository.save(wine)).thenReturn(wine);
        when(wineMapper.toDto(wine)).thenReturn(expectedWineDto);

        // When
        WineDto actual = wineService.save(requestDto);

        // Then
        assertEquals(expectedWineDto, actual);
        verify(wineRepository).save(wine);
        verify(wineMapper).toDto(wine);
    }

    @Test
    @DisplayName("""
            Find wine by id when it exists and return the corresponding wine DTO
            """)
    void findWine_ExistingWineId_ReturnsWineDto() {
        // Given
        Wine wine = getWine();
        WineDto expectedWineDto = getWineDto();

        when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
        when(wineMapper.toDto(wine)).thenReturn(expectedWineDto);

        // When
        WineDto actual = wineService.findById(wine.getId());

        // Then
        assertEquals(expectedWineDto, actual);
        verify(wineRepository).findById(wine.getId());
        verify(wineMapper).toDto(wine);
    }

    @Test
    @DisplayName("""
            Find all wines with valid pageable and return a page of wine DTOs
            """)
    void findAllWines_ValidPageable_ReturnsPageOfWineDto() {
        // Given
        Wine wine = getWine();
        WineDto expectedWineDto = getWineDto();
        List<Wine> wines = List.of(wine);
        Page<Wine> winePage = new PageImpl<>(wines, PageRequest.of(0, 5), wines.size());

        when(wineRepository.findAll(PageRequest.of(0, 5))).thenReturn(winePage);
        when(wineMapper.toDto(wine)).thenReturn(expectedWineDto);

        // When
        Page<WineDto> actual = wineService.findAll(PageRequest.of(0, 5));

        // Then
        assertEquals(1, actual.getTotalElements());
        assertEquals(expectedWineDto, actual.getContent().get(0));
        verify(wineRepository).findAll(PageRequest.of(0, 5));
        verify(wineMapper).toDto(wine);
    }

    @Test
    @DisplayName("""
            Find wines by regionId and return a page of wine DTOs without region
            """)
    void findWinesByRegionId_ExistingRegionId_ReturnsWineDtoPage() {
        // Given
        Wine wine = getWine();
        WineDtoWithoutRegion expectedWineDtoWithoutRegion = getWineDtoWithoutRegion();
        List<Wine> wines = List.of(wine);
        Page<Wine> winePage = new PageImpl<>(wines, PageRequest.of(0, 5), wines.size());

        when(wineRepository.findAllByRegionId(
                wine.getRegion().getId(), PageRequest.of(0, 5))).thenReturn(winePage);
        when(wineMapper.toDtoWithoutRegions(wine)).thenReturn(expectedWineDtoWithoutRegion);

        // When
        Page<WineDtoWithoutRegion> actual = wineService.findAllByRegionId(
                wine.getRegion().getId(), PageRequest.of(0, 5));

        // Then
        assertEquals(1, actual.getTotalElements());
        verify(wineRepository).findAllByRegionId(wine.getRegion().getId(), PageRequest.of(0, 5));
        verify(wineMapper).toDtoWithoutRegions(wine);
    }

    @Test
    @DisplayName("""
            Update wine by id and return the updated wine DTO
            """)
    void updateWine_ExistingId_ReturnsWineDto() {
        // Given
        Wine wine = getWine();
        CreateWineRequestDto updateRequestDto = new CreateWineRequestDto(
                "Updated Wine",
                BigDecimal.valueOf(25.0),
                "Chardonnay",
                wine.getRegion().getId());
        WineDto expectedWineDto = getWineDto();

        when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
        when(regionRepository.findById(wine.getRegion().getId()))
                .thenReturn(Optional.of(wine.getRegion()));
        when(wineRepository.save(wine)).thenReturn(wine);
        when(wineMapper.toDto(wine)).thenReturn(expectedWineDto);

        // When
        WineDto actual = wineService.updateById(wine.getId(), updateRequestDto);

        // Then
        assertEquals(expectedWineDto, actual);
        verify(wineRepository).findById(wine.getId());
        verify(regionRepository).findById(wine.getRegion().getId());
        verify(wineRepository).save(wine);
        verify(wineMapper).toDto(wine);
    }

    @Test
    @DisplayName("""
            Delete wine by id successfully
            """)
    void deleteWine_ExistingId_DeletesSuccessfully() {
        // Given
        Wine wine = getWine();
        doNothing().when(wineRepository).deleteById(wine.getId());

        // When
        wineService.deleteById(wine.getId());

        // Then
        verify(wineRepository).deleteById(wine.getId());
    }

    private CreateWineRequestDto getCreateWineRequestDto() {
        return new CreateWineRequestDto("Wine A", BigDecimal.valueOf(20.0), "Merlot", 1L);
    }

    private Wine getWine() {
        Region region = new Region();
        region.setId(1L);
        region.setName("Napa Valley");
        region.setCountry("USA");

        Wine wine = new Wine();
        wine.setId(1L);
        wine.setName("Wine A");
        wine.setPrice(BigDecimal.valueOf(20.0));
        wine.setGrapeVariety("Merlot");
        wine.setRegion(region);
        return wine;
    }

    private WineDto getWineDto() {
        return new WineDto()
                .setId(1L)
                .setName("Wine A")
                .setPrice(BigDecimal.valueOf(20.0))
                .setGrapeVariety("Merlot")
                .setRegionId(1L);
    }

    private WineDtoWithoutRegion getWineDtoWithoutRegion() {
        return new WineDtoWithoutRegion("Wine A", BigDecimal.valueOf(20.0), "Merlot");
    }
}
