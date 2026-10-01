package com.fashionsense.catalog.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal basePrice,
        boolean active,

        Long brandId,
        String brandName,
        String brandSlug,

        Long categoryId,
        String categoryName,
        String categorySlug,

        List<String> occasions,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponse from(Product product) {

        List<String> occasions = product.getOccasions()
                .stream()
                .map(occasion -> occasion.getSlug())
                .sorted()
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getBasePrice(),
                product.isActive(),

                product.getBrand().getId(),
                product.getBrand().getName(),
                product.getBrand().getSlug(),

                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),

                occasions,

                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}