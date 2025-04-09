package com.example.winehood.dto.wine;

public record WineSearchParametersDto(
        String[] names,
        String[] grapeVarieties,
        String[] regionNames) {
}
