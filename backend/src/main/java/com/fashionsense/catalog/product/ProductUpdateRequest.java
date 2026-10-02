package com.fashionsense.catalog.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(

        @NotNull(message = "Brand ID is required")
        Long brandId,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        @NotBlank(message = "Product name is required")
        @Size(
                max = 200,
                message = "Product name must not exceed 200 characters"
        )
        String name,

        String description,

        @NotNull(message = "Base price is required")
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Base price cannot be negative"
        )
        BigDecimal basePrice,

        boolean active
) {
}