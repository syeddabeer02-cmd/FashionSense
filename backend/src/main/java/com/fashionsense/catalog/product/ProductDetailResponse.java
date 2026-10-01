package com.fashionsense.catalog.product;

import com.fashionsense.catalog.image.ProductImageResponse;
import com.fashionsense.catalog.variant.ProductVariantResponse;

import java.util.List;

public record ProductDetailResponse(
        ProductResponse product,
        List<ProductVariantResponse> variants,
        List<ProductImageResponse> images
) {
}