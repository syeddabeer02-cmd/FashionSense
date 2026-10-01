package com.fashionsense.catalog.image;

public class ProductImageNotFoundException extends RuntimeException {

    public ProductImageNotFoundException(String message) {
        super(message);
    }
}