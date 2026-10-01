package com.fashionsense.customer;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentUserResponse(
        Long userId,
        String email,
        String role
) {

    public static CurrentUserResponse from(Jwt jwt) {

        Number userId =
                jwt.getClaim("userId");

        return new CurrentUserResponse(
                userId.longValue(),
                jwt.getSubject(),
                jwt.getClaimAsString("role")
        );
    }
}