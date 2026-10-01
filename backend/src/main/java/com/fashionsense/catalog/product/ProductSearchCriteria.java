package com.fashionsense.catalog.product;

import java.math.BigDecimal;

public record ProductSearchCriteria(
        String brand,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String size,
        String color,
        String style,
        String material,
        String occasion
) {
}