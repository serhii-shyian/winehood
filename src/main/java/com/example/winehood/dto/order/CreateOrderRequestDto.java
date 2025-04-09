package com.example.winehood.dto.order;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateOrderRequestDto(
        @NotBlank(message = "ShippingAddress may not be blank")
        @Length(min = 10, max = 255)
        String shippingAddress) {
}
