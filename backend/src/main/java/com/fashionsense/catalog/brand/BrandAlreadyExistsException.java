package com.fashionsense.catalog.brand;

public class BrandAlreadyExistsException extends RuntimeException {

    public BrandAlreadyExistsException(String message) {
        super(message);
    }
}