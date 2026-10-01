package com.fashionsense.cart;

import com.fashionsense.catalog.variant.ProductVariant;

import java.math.BigDecimal;

public record CartItemResponse(
        Long cartItemId,
        Long variantId,
        String sku,
        String productName,
        String productSlug,
        String size,
        String color,
        String style,
        String material,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal,
        String availability
) {

    public static CartItemResponse from(
            CartItem item
    ) {

        ProductVariant variant =
                item.getVariant();

        BigDecimal unitPrice =
                variant.getPrice() != null
                        ? variant.getPrice()
                        : variant.getProduct().getBasePrice();

        BigDecimal lineTotal =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                item.getQuantity()
                        )
                );

        String availability =
                variant.isActive()
                        && variant.getStockQuantity() > 0
                        ? "IN_STOCK"
                        : "OUT_OF_STOCK";

        return new CartItemResponse(
                item.getId(),
                variant.getId(),
                variant.getSku(),
                variant.getProduct().getName(),
                variant.getProduct().getSlug(),
                variant.getSize(),
                variant.getColor(),
                variant.getStyle(),
                variant.getMaterial(),
                unitPrice,
                item.getQuantity(),
                lineTotal,
                availability
        );
    }
}