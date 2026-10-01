package com.fashionsense.customer.address;

public record AddressResponse(
        Long id,
        String label,
        String recipientName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String countryCode,
        String phone,
        boolean defaultAddress
) {

    public static AddressResponse from(Address address) {

        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getRecipientName(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountryCode(),
                address.getPhone(),
                address.isDefaultAddress()
        );
    }
}