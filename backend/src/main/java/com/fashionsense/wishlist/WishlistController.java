package com.fashionsense.wishlist;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/me/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(
            WishlistService wishlistService
    ) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public List<WishlistItemResponse> getWishlist(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return wishlistService.getWishlist(
                getUserId(jwt)
        );
    }

    @PutMapping("/{productSlug}")
    public WishlistItemResponse addProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productSlug
    ) {

        return wishlistService.addProduct(
                getUserId(jwt),
                productSlug
        );
    }

    @DeleteMapping("/{productSlug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productSlug
    ) {

        wishlistService.removeProduct(
                getUserId(jwt),
                productSlug
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