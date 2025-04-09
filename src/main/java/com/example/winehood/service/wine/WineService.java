package com.example.winehood.service.wine;

import com.example.winehood.dto.wine.CreateWineRequestDto;
import com.example.winehood.dto.wine.WineDto;
import com.example.winehood.dto.wine.WineDtoWithoutRegion;
import com.example.winehood.dto.wine.WineSearchParametersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WineService {
    WineDto save(CreateWineRequestDto requestDto);

    WineDto findById(Long wineId);

    Page<WineDto> findAll(Pageable pageable);

    Page<WineDtoWithoutRegion> findAllByRegionId(Long regionId, Pageable pageable);

    WineDto updateById(Long wineId, CreateWineRequestDto requestDto);

    void deleteById(Long wineId);

    Page<WineDto> searchByParameters(WineSearchParametersDto paramsDto, Pageable pageable);
}
