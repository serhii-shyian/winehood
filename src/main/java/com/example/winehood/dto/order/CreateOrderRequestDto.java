package com.example.winehood.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.Length;

public record CreateOrderRequestDto(
        @NotNull(message = "Order date is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime orderDate,
        @NotBlank(message = "ShippingAddress may not be blank")
        @Length(min = 10, max = 255)
        String shippingAddress) {
}
