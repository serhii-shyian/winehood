package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.wine.CreateWineRequestDto;
import com.example.winehood.dto.wine.WineDto;
import com.example.winehood.dto.wine.WineDtoWithoutRegion;
import com.example.winehood.model.Wine;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface WineMapper {
    WineDto toDto(Wine wine);

    Wine toEntity(CreateWineRequestDto requestDto);

    void updateEntityFromDto(CreateWineRequestDto requestDto, @MappingTarget Wine wine);

    WineDtoWithoutRegion toDtoWithoutRegions(Wine wine);

    @AfterMapping
    default void setRegionId(@MappingTarget WineDto wineDto, Wine wine) {
        Long regionId = wine.getRegion().getId();
        wineDto.setRegionId(regionId);
    }
}
