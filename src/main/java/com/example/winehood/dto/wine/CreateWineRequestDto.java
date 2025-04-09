package com.example.winehood.dto.wine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateWineRequestDto(
        @NotBlank(message = "Name may not be blank")
        String name,
        @NotNull(message = "Price may not be null")
        @Positive
        BigDecimal price,
        @NotBlank(message = "Grape variety may not be blank")
        String grapeVariety,
        @NotNull(message = "Region id may not be blank")
        Long regionId) {
}
