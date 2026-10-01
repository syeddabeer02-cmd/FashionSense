package com.fashionsense.catalog.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BrandRequest(

        @NotBlank(message = "Brand name is required")
        @Size(max = 100, message = "Brand name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Brand slug is required")
        @Size(max = 120, message = "Brand slug must not exceed 120 characters")
        String slug,

        @Size(max = 500, message = "Logo URL must not exceed 500 characters")
        String logoUrl,

        String description
) {
}