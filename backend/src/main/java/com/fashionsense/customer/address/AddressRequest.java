package com.fashionsense.customer.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @Size(max = 50)
        String label,

        @NotBlank
        @Size(max = 200)
        String recipientName,

        @NotBlank
        @Size(max = 255)
        String addressLine1,

        @Size(max = 255)
        String addressLine2,

        @NotBlank
        @Size(max = 120)
        String city,

        @NotBlank
        @Size(max = 120)
        String state,

        @NotBlank
        @Size(max = 30)
        String postalCode,

        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{2}$")
        String countryCode,

        @Size(max = 30)
        String phone,

        boolean defaultAddress

) {
}