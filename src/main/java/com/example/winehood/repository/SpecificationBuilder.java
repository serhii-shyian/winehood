package com.example.winehood.repository;

import com.example.winehood.dto.wine.WineSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(WineSearchParametersDto searchParameters);
}
