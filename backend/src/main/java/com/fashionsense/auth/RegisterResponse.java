package com.fashionsense.auth;

import com.fashionsense.customer.User;

public record RegisterResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role,
        String status,
        boolean emailVerified
) {

    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getStatus().name(),
                user.isEmailVerified()
        );
    }
}
