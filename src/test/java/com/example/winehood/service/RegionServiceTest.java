package com.example.winehood.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.winehood.dto.region.CreateRegionRequestDto;
import com.example.winehood.dto.region.RegionDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.RegionMapper;
import com.example.winehood.model.Region;
import com.example.winehood.repository.region.RegionRepository;
import com.example.winehood.service.region.RegionServiceImpl;
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
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {
    @InjectMocks
    private RegionServiceImpl regionService;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private RegionMapper regionMapper;

    @Test
    @DisplayName("""
            Save valid region request DTO returns region DTO
            """)
    void save_ValidRequestDto_ReturnsRegionDto() {
        // Given
        CreateRegionRequestDto requestDto = getCreateRegionRequestDto("Test Region");
        Region region = getRegion("Test Region");
        RegionDto expectedDto = getRegionDto(1L, "Test Region");

        when(regionMapper.toEntity(requestDto)).thenReturn(region);
        when(regionRepository.save(region)).thenReturn(region);
        when(regionMapper.toDto(region)).thenReturn(expectedDto);

        // When
        RegionDto actualDto = regionService.save(requestDto);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(regionMapper).toEntity(requestDto);
        verify(regionRepository).save(region);
        verify(regionMapper).toDto(region);
    }

    @Test
    @DisplayName("""
            Find by existing ID returns region DTO
            """)
    void findById_ExistingId_ReturnsRegionDto() {
        // Given
        Long id = 1L;
        Region region = getRegion("Region One");
        RegionDto expectedDto = getRegionDto(id, "Region One");

        when(regionRepository.findById(id)).thenReturn(Optional.of(region));
        when(regionMapper.toDto(region)).thenReturn(expectedDto);

        // When
        RegionDto actualDto = regionService.findById(id);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(regionRepository).findById(id);
        verify(regionMapper).toDto(region);
    }

    @Test
    @DisplayName("""
            Find by non-existing ID throws exception
            """)
    void findById_NonExistingId_ThrowsException() {
        // Given
        Long id = 99L;
        when(regionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> regionService.findById(id));
        verify(regionRepository).findById(id);
    }

    @Test
    @DisplayName("""
            Find all valid pageable returns page of region DTO
            """)
    void findAll_ValidPageable_ReturnsPageOfRegionDto() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Region region1 = getRegion("Region One");
        Region region2 = getRegion("Region Two");
        RegionDto dto1 = getRegionDto(1L, "Region One");
        RegionDto dto2 = getRegionDto(2L, "Region Two");
        Page<Region> regionPage = new PageImpl<>(List.of(region1, region2));

        when(regionRepository.findAll(pageable)).thenReturn(regionPage);
        when(regionMapper.toDto(region1)).thenReturn(dto1);
        when(regionMapper.toDto(region2)).thenReturn(dto2);

        // When
        Page<RegionDto> result = regionService.findAll(pageable);

        // Then
        assertEquals(List.of(dto1, dto2), result.getContent());
        verify(regionRepository).findAll(pageable);
        verify(regionMapper).toDto(region1);
        verify(regionMapper).toDto(region2);
    }

    @Test
    @DisplayName("""
            Update by existing ID returns updated DTO
            """)
    void updateById_ExistingId_ReturnsUpdatedDto() {
        // Given
        Long id = 1L;
        CreateRegionRequestDto requestDto = getCreateRegionRequestDto("Updated");
        Region existingRegion = getRegion("Old Name");
        RegionDto updatedDto = getRegionDto(id, "Updated");

        when(regionRepository.findById(id)).thenReturn(Optional.of(existingRegion));
        doNothing().when(regionMapper).updateEntityFromDto(requestDto, existingRegion);
        when(regionRepository.save(existingRegion)).thenReturn(existingRegion);
        when(regionMapper.toDto(existingRegion)).thenReturn(updatedDto);

        // When
        RegionDto result = regionService.updateById(id, requestDto);

        // Then
        assertEquals(updatedDto, result);
        verify(regionRepository).findById(id);
        verify(regionMapper).updateEntityFromDto(requestDto, existingRegion);
        verify(regionRepository).save(existingRegion);
        verify(regionMapper).toDto(existingRegion);
    }

    private CreateRegionRequestDto getCreateRegionRequestDto(String name) {
        return new CreateRegionRequestDto(name, "Ukraine");
    }

    private Region getRegion(String name) {
        Region region = new Region();
        region.setName(name);
        return region;
    }

    private RegionDto getRegionDto(Long id, String name) {
        return new RegionDto(id, name, "Ukraine");
    }
}
