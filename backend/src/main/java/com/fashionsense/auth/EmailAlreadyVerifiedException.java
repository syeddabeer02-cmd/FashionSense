package com.fashionsense.auth;

public class EmailAlreadyVerifiedException
        extends RuntimeException {

    public EmailAlreadyVerifiedException(
            String message
    ) {
        super(message);
    }
}