package com.example.winehood.dto.region;

import jakarta.validation.constraints.NotBlank;

public record CreateRegionRequestDto(
        @NotBlank(message = "Name may not be blank")
        String name,
        @NotBlank(message = "Country may not be blank")
        String country) {
}
