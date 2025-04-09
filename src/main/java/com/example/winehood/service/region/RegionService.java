package com.example.winehood.service.region;

import com.example.winehood.dto.region.CreateRegionRequestDto;
import com.example.winehood.dto.region.RegionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegionService {
    RegionDto save(CreateRegionRequestDto requestDto);

    RegionDto findById(Long regionId);

    Page<RegionDto> findAll(Pageable pageable);

    RegionDto updateById(Long regionId, CreateRegionRequestDto requestDto);

    void deleteById(Long regionId);
}
