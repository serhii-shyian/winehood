package com.example.winehood.repository.wine;

import com.example.winehood.dto.wine.WineSearchParametersDto;
import com.example.winehood.model.Wine;
import com.example.winehood.repository.SpecificationBuilder;
import com.example.winehood.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor()
public class WineSpecificationBuilder implements SpecificationBuilder<Wine> {
    private final SpecificationProviderManager<Wine> providerManager;

    @Override
    public Specification<Wine> build(WineSearchParametersDto paramsDto) {
        Specification<Wine> spec = Specification.where(null);
        if (paramsDto.names() != null && paramsDto.names().length > 0) {
            spec = spec.and(providerManager.getSpecificationProvider("name")
                    .getSpecification(paramsDto.names()));
        }
        if (paramsDto.grapeVarieties() != null && paramsDto.grapeVarieties().length > 0) {
            spec = spec.and(providerManager.getSpecificationProvider("grapeVariety")
                    .getSpecification(paramsDto.grapeVarieties()));
        }
        if (paramsDto.regionNames() != null && paramsDto.regionNames().length > 0) {
            spec = spec.and(providerManager.getSpecificationProvider("region")
                    .getSpecification(paramsDto.regionNames()));
        }
        return spec;
    }
}
