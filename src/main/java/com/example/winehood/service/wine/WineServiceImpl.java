package com.example.winehood.service.wine;

import com.example.winehood.dto.wine.CreateWineRequestDto;
import com.example.winehood.dto.wine.WineDto;
import com.example.winehood.dto.wine.WineDtoWithoutRegion;
import com.example.winehood.dto.wine.WineSearchParametersDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.WineMapper;
import com.example.winehood.model.Region;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.region.RegionRepository;
import com.example.winehood.repository.wine.WineRepository;
import com.example.winehood.repository.wine.WineSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WineServiceImpl implements WineService {
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    private final WineSpecificationBuilder wineSpecificationBuilder;
    private final RegionRepository regionRepository;

    @Override
    public WineDto save(CreateWineRequestDto requestDto) {
        Wine wineFromDto = wineMapper.toEntity(requestDto);
        wineFromDto.setRegion(getRegionsFromDto(requestDto));
        return wineMapper.toDto(wineRepository.save(wineFromDto));
    }

    @Override
    public WineDto findById(Long wineId) {
        Wine wineFromDb = findWineById(wineId);
        return wineMapper.toDto(wineFromDb);
    }

    @Override
    public Page<WineDto> findAll(Pageable pageable) {
        return wineRepository.findAll(pageable)
                .map(wineMapper::toDto);
    }

    @Override
    public Page<WineDtoWithoutRegion> findAllByRegionId(Long regionId, Pageable pageable) {
        return wineRepository.findAllByRegionId(regionId, pageable)
                .map(wineMapper::toDtoWithoutRegions);
    }

    @Override
    public WineDto updateById(Long wineId, CreateWineRequestDto requestDto) {
        Wine wineFromDb = findWineById(wineId);
        wineFromDb.setRegion(getRegionsFromDto(requestDto));
        wineMapper.updateEntityFromDto(requestDto, wineFromDb);
        return wineMapper.toDto(wineRepository.save(wineFromDb));
    }

    @Override
    public void deleteById(Long wineId) {
        wineRepository.deleteById(wineId);
    }

    @Override
    public Page<WineDto> searchByParameters(WineSearchParametersDto paramsDto, Pageable pageable) {
        Specification<Wine> wineSpecification = wineSpecificationBuilder.build(paramsDto);
        return wineRepository.findAll(wineSpecification, pageable)
                .map(wineMapper::toDto);
    }

    private Wine findWineById(Long wineId) {
        return wineRepository.findById(wineId).orElseThrow(
                () -> new EntityNotFoundException("Can't find wine by id: " + wineId));
    }

    private Region getRegionsFromDto(CreateWineRequestDto requestDto) {
        return regionRepository.findById(requestDto.regionId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find region by id: " + requestDto.regionId()));
    }
}
