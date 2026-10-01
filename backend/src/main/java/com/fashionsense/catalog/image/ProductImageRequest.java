package com.fashionsense.catalog.image;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Image URL is required")
        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        @Size(max = 255, message = "Alt text must not exceed 255 characters")
        String altText,

        @Min(
                value = 0,
                message = "Display order cannot be negative"
        )
        int displayOrder,

        boolean primaryImage
) {
}
