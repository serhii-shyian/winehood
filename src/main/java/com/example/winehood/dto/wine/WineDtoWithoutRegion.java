package com.example.winehood.dto.wine;

import java.math.BigDecimal;

public record WineDtoWithoutRegion(
        String name,
        BigDecimal price,
        String grapeVariety) {
}
