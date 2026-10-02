package com.fashionsense.catalog.variant;

import java.math.BigDecimal;

public record AdminProductVariantResponse(
        Long id,
        Long productId,
        String sku,
        String size,
        String color,
        String style,
        String material,
        BigDecimal price,
        int stockQuantity,
        boolean active
) {

    public static AdminProductVariantResponse from(
            ProductVariant variant
    ) {

        return new AdminProductVariantResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getSize(),
                variant.getColor(),
                variant.getStyle(),
                variant.getMaterial(),
                variant.getPrice(),
                variant.getStockQuantity(),
                variant.isActive()
        );
    }
}