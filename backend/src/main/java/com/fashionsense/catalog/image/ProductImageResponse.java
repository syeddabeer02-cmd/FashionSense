package com.fashionsense.catalog.image;

public record ProductImageResponse(
        Long id,
        Long productId,
        String imageUrl,
        String altText,
        int displayOrder,
        boolean primaryImage
) {

    public static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getProduct().getId(),
                image.getImageUrl(),
                image.getAltText(),
                image.getDisplayOrder(),
                image.isPrimaryImage()
        );
    }
}