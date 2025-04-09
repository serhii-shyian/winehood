package com.example.winehood.repository.wine.spec;

import com.example.winehood.model.Wine;
import com.example.winehood.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NameSpecificationProvider implements SpecificationProvider<Wine> {
    private static final String KEY = "name";

    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Wine> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(KEY).in(Arrays.stream(params).toArray());
    }
}
