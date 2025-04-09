package com.example.winehood.service.region;

import com.example.winehood.dto.region.CreateRegionRequestDto;
import com.example.winehood.dto.region.RegionDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.RegionMapper;
import com.example.winehood.model.Region;
import com.example.winehood.repository.region.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Override
    public RegionDto save(CreateRegionRequestDto requestDto) {
        Region regionFromDto = regionMapper.toEntity(requestDto);
        return regionMapper.toDto(regionRepository.save(regionFromDto));
    }

    @Override
    public RegionDto findById(Long regionId) {
        Region regionFromDb = findRegionById(regionId);
        return regionMapper.toDto(regionFromDb);
    }

    @Override
    public Page<RegionDto> findAll(Pageable pageable) {
        return regionRepository.findAll(pageable)
                .map(regionMapper::toDto);
    }

    @Override
    public RegionDto updateById(Long regionId, CreateRegionRequestDto requestDto) {
        Region regionFromDb = findRegionById(regionId);
        regionMapper.updateEntityFromDto(requestDto, regionFromDb);
        return regionMapper.toDto(regionRepository.save(regionFromDb));
    }

    @Override
    public void deleteById(Long regionId) {
        regionRepository.deleteById(regionId);
    }

    private Region findRegionById(Long regionId) {
        return regionRepository.findById(regionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find region by id: " + regionId));
    }
}
