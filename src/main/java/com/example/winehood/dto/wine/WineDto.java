package com.example.winehood.dto.wine;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WineDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String grapeVariety;
    private Long regionId;
}
