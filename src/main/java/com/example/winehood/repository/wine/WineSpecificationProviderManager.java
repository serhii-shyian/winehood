package com.example.winehood.repository.wine;

import com.example.winehood.model.Wine;
import com.example.winehood.repository.SpecificationProvider;
import com.example.winehood.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor()
public class WineSpecificationProviderManager implements SpecificationProviderManager<Wine> {
    private final List<SpecificationProvider<Wine>> winesSpecificationProviders;

    @Override
    public SpecificationProvider<Wine> getSpecificationProvider(String key) {
        return winesSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No specification provider found for key: " + key));
    }
}
