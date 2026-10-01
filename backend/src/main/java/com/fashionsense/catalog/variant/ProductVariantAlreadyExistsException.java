package com.fashionsense.catalog.variant;

public class ProductVariantAlreadyExistsException extends RuntimeException {

    public ProductVariantAlreadyExistsException(String message) {
        super(message);
    }
}