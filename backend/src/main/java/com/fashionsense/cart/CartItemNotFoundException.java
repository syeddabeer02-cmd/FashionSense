package com.fashionsense.cart;

public class CartItemNotFoundException
        extends RuntimeException {

    public CartItemNotFoundException(
            String message
    ) {
        super(message);
    }
}