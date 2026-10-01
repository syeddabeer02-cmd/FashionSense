package com.fashionsense.auth;

public class EmailAlreadyRegisteredException
        extends RuntimeException {

    public EmailAlreadyRegisteredException(
            String message
    ) {
        super(message);
    }
}