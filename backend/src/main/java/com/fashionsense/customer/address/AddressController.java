package com.fashionsense.customer.address;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/me/addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService
    ) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<AddressResponse> getAddresses(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return addressService.getAddresses(
                getUserId(jwt)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse createAddress(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            AddressRequest request
    ) {

        return addressService.createAddress(
                getUserId(jwt),
                request
        );
    }

    @PutMapping("/{addressId}/default")
    public AddressResponse setDefaultAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long addressId
    ) {

        return addressService.setDefaultAddress(
                getUserId(jwt),
                addressId
        );
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long addressId
    ) {

        addressService.deleteAddress(
                getUserId(jwt),
                addressId
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