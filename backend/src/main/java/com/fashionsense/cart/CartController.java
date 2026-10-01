package com.fashionsense.cart;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/me/cart")
public class CartController {

    private final CartService cartService;

    public CartController(
            CartService cartService
    ) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return cartService.getCart(
                getUserId(jwt)
        );
    }

    @PostMapping("/items")
    public CartResponse addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            AddCartItemRequest request
    ) {

        return cartService.addItem(
                getUserId(jwt),
                request
        );
    }

    @PutMapping("/items/{cartItemId}")
    public CartResponse updateQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cartItemId,
            @Valid
            @RequestBody
            UpdateCartItemRequest request
    ) {

        return cartService.updateQuantity(
                getUserId(jwt),
                cartItemId,
                request
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cartItemId
    ) {

        return cartService.removeItem(
                getUserId(jwt),
                cartItemId
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