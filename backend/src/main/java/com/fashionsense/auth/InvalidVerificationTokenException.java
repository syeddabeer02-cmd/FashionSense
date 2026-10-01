package com.fashionsense.auth;

public class InvalidVerificationTokenException
        extends RuntimeException {

    public InvalidVerificationTokenException(
            String message
    ) {
        super(message);
    }
}