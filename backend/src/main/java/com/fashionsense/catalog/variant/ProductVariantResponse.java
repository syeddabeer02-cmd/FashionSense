package com.fashionsense.catalog.variant;

import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        Long productId,
        String sku,
        String size,
        String color,
        String style,
        String material,
        BigDecimal price,
        String availability,
        boolean active
) {

    public static ProductVariantResponse from(ProductVariant variant) {

        BigDecimal effectivePrice =
                variant.getPrice() != null
                        ? variant.getPrice()
                        : variant.getProduct().getBasePrice();

        String availability =
                variant.isActive() && variant.getStockQuantity() > 0
                        ? "IN_STOCK"
                        : "OUT_OF_STOCK";

        return new ProductVariantResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getSize(),
                variant.getColor(),
                variant.getStyle(),
                variant.getMaterial(),
                effectivePrice,
                availability,
                variant.isActive()
        );
    }
}