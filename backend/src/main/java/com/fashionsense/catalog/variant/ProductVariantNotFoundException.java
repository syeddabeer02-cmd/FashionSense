package com.fashionsense.catalog.variant;

public class ProductVariantNotFoundException extends RuntimeException {

    public ProductVariantNotFoundException(String message) {
        super(message);
    }
}