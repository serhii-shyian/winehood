package com.example.winehood.controller;

import com.example.winehood.dto.region.CreateRegionRequestDto;
import com.example.winehood.dto.region.RegionDto;
import com.example.winehood.dto.wine.WineDtoWithoutRegion;
import com.example.winehood.service.region.RegionService;
import com.example.winehood.service.wine.WineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
@Tag(name = "Region management", description = "Endpoint for managing regions")
@Validated
public class RegionController {
    private final RegionService regionService;
    private final WineService wineService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all regions",
            description = "Getting a page of all available regions")
    @PreAuthorize("hasRole('USER')")
    public Page<RegionDto> getAll(@ParameterObject
                                  @PageableDefault(
                                          size = 5,
                                          sort = "country",
                                          direction = Sort.Direction.ASC)
                                  Pageable pageable) {
        return regionService.findAll(pageable);
    }

    @GetMapping("/{regionId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a region by id",
            description = "Getting a region by id if available")
    @PreAuthorize("hasRole('USER')")
    public RegionDto getRegionById(@PathVariable @Positive Long regionId) {
        return regionService.findById(regionId);
    }

    @GetMapping("/{regionId}/wines")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all wines by region id",
            description = "Getting all wines by region id if available")
    @PreAuthorize("hasRole('USER')")
    public Page<WineDtoWithoutRegion> getWinesByRegionId(
            @PathVariable @Positive Long regionId,
            @ParameterObject @PageableDefault(
                    size = 5,
                    sort = "name",
                    direction = Sort.Direction.ASC)
            Pageable pageable) {
        return wineService.findAllByRegionId(regionId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new region",
            description = "Creating  a new region according to the parameters")
    @PreAuthorize("hasRole('ADMIN')")
    public RegionDto createRegion(@RequestBody @Valid CreateRegionRequestDto requestDto) {
        return regionService.save(requestDto);
    }

    @PutMapping("/{regionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update a region by id",
            description = "Updating a region by id according to the parameters")
    @PreAuthorize("hasRole('ADMIN')")
    public RegionDto updateRegionById(
            @PathVariable @Positive Long regionId,
            @RequestBody @Valid CreateRegionRequestDto requestDto) {
        return regionService.updateById(regionId, requestDto);
    }

    @DeleteMapping("/{regionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a region by id",
            description = "Deleting a region by id if available")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRegionById(@PathVariable @Positive Long regionId) {
        regionService.deleteById(regionId);
    }
}
