package com.fashionsense.order;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/me/checkout")
@SecurityRequirement(name = "bearerAuth")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(
            CheckoutService checkoutService
    ) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public OrderResponse checkout(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            CheckoutRequest request
    ) {

        return checkoutService.checkout(
                getUserId(jwt),
                request
        );
    }

    private Long getUserId(
            Jwt jwt
    ) {

        Number userId =
                jwt.getClaim("userId");

        return userId.longValue();
    }
}