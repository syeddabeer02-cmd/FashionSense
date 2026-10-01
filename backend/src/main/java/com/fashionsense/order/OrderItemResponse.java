package com.fashionsense.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String sku,
        String productName,
        String size,
        String color,
        String style,
        String material,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {

    public static OrderItemResponse from(
            OrderItem item
    ) {

        return new OrderItemResponse(
                item.getId(),
                item.getSku(),
                item.getProductName(),
                item.getSize(),
                item.getColor(),
                item.getStyle(),
                item.getMaterial(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }
}