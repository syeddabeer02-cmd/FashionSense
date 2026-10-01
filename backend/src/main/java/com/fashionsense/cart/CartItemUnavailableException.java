package com.fashionsense.cart;

public class CartItemUnavailableException
        extends RuntimeException {

    public CartItemUnavailableException(
            String message
    ) {
        super(message);
    }
}