package com.fashionsense.auth;

public class AccountNotActiveException
        extends RuntimeException {

    public AccountNotActiveException(String message) {
        super(message);
    }
}
