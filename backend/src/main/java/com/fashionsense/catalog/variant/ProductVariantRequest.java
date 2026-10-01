package com.fashionsense.catalog.variant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductVariantRequest(

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "SKU is required")
        @Size(max = 100, message = "SKU must not exceed 100 characters")
        String sku,

        @Size(max = 50, message = "Size must not exceed 50 characters")
        String size,

        @Size(max = 50, message = "Color must not exceed 50 characters")
        String color,

        @Size(max = 100, message = "Style must not exceed 100 characters")
        String style,

        @Size(max = 100, message = "Material must not exceed 100 characters")
        String material,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Variant price cannot be negative"
        )
        BigDecimal price,

        @Min(
                value = 0,
                message = "Stock quantity cannot be negative"
        )
        int stockQuantity
) {
}