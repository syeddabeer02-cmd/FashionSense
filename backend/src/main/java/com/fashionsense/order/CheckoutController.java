package com.fashionsense.order;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/me/checkout")
@SecurityRequirement(name = "bearerAuth")
public class CheckoutController {

    private static final int MAX_IDEMPOTENCY_KEY_LENGTH = 100;

    private final CheckoutService checkoutService;

    public CheckoutController(
            CheckoutService checkoutService
    ) {
        this.checkoutService =
                checkoutService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Idempotency-Key")
            String idempotencyKey,
            @Valid @RequestBody
            CheckoutRequest request
    ) {
        String normalizedIdempotencyKey =
                validateIdempotencyKey(
                        idempotencyKey
                );

        return checkoutService.checkout(
                getUserId(jwt),
                request,
                normalizedIdempotencyKey
        );
    }

    private String validateIdempotencyKey(
            String idempotencyKey
    ) {
        String normalized =
                idempotencyKey == null
                        ? ""
                        : idempotencyKey.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Idempotency-Key must not be blank"
            );
        }

        if (normalized.length()
                > MAX_IDEMPOTENCY_KEY_LENGTH) {

            throw new IllegalArgumentException(
                    "Idempotency-Key must not exceed 100 characters"
            );
        }

        return normalized;
    }

    private Long getUserId(
            Jwt jwt
    ) {
        return Long.valueOf(
                jwt.getClaimAsString(
                        "userId"
                )
        );
    }
}