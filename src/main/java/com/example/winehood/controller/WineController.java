package com.example.winehood.controller;

import com.example.winehood.dto.wine.CreateWineRequestDto;
import com.example.winehood.dto.wine.WineDto;
import com.example.winehood.dto.wine.WineSearchParametersDto;
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
@RequestMapping("/wines")
@Tag(name = "Wine management", description = "Endpoint for managing wines")
@Validated
public class WineController {
    private final WineService wineService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all wines",
            description = "Getting a list of all available wines")
    @PreAuthorize("hasRole('USER')")
    public Page<WineDto> getAll(@ParameterObject
                                @PageableDefault(
                                        size = 5,
                                        sort = "name",
                                        direction = Sort.Direction.ASC)
                                Pageable pageable) {
        return wineService.findAll(pageable);
    }

    @GetMapping("/{wineId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a wine by id",
            description = "Getting a wine by id if available")
    @PreAuthorize("hasRole('USER')")
    public WineDto getWineById(@PathVariable @Positive Long wineId) {
        return wineService.findById(wineId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all wines by parameters",
            description = "Getting a list of all wines according to the parameters")
    @PreAuthorize("hasRole('USER')")
    public Page<WineDto> searchWines(WineSearchParametersDto searchParameters,
                                     @ParameterObject
                                     @PageableDefault(
                                             size = 5,
                                             sort = "name",
                                             direction = Sort.Direction.ASC)
                                     Pageable pageable) {
        return wineService.searchByParameters(searchParameters, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new wine",
            description = "Creating a new wine according to the parameters")
    @PreAuthorize("hasRole('ADMIN')")
    public WineDto createWine(@RequestBody @Valid CreateWineRequestDto requestDto) {
        return wineService.save(requestDto);
    }

    @PutMapping("/{wineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update a wine by id",
            description = "Updating a wine by id according to the parameters")
    @PreAuthorize("hasRole('ADMIN')")
    public WineDto updateWineById(@PathVariable @Positive Long wineId,
                                  @RequestBody @Valid CreateWineRequestDto requestDto) {
        return wineService.updateById(wineId, requestDto);
    }

    @DeleteMapping("/{wineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a wine by id",
            description = "Deleting a wine by id if available")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteWineById(@PathVariable @Positive Long wineId) {
        wineService.deleteById(wineId);
    }
}
