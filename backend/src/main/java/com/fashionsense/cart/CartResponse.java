package com.fashionsense.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        BigDecimal subtotal
) {

    public static CartResponse from(
            Cart cart,
            List<CartItem> items
    ) {

        List<CartItemResponse> responses =
                items.stream()
                        .map(CartItemResponse::from)
                        .toList();

        BigDecimal subtotal =
                responses.stream()
                        .map(CartItemResponse::lineTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new CartResponse(
                cart.getId(),
                responses,
                subtotal
        );
    }
}