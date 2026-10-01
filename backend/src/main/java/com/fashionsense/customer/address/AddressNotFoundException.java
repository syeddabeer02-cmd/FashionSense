package com.fashionsense.customer.address;

public class AddressNotFoundException
        extends RuntimeException {

    public AddressNotFoundException(
            String message
    ) {
        super(message);
    }
}