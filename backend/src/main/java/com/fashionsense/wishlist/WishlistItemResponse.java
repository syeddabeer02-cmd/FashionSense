package com.fashionsense.wishlist;

import com.fashionsense.catalog.product.ProductResponse;

import java.time.LocalDateTime;

public record WishlistItemResponse(
        Long wishlistItemId,
        ProductResponse product,
        LocalDateTime addedAt
) {

    public static WishlistItemResponse from(
            WishlistItem item
    ) {

        return new WishlistItemResponse(
                item.getId(),
                ProductResponse.from(
                        item.getProduct()
                ),
                item.getCreatedAt()
        );
    }
}