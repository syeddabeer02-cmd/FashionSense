package com.fashionsense.order;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(

        @NotNull
        Long addressId,

        @NotNull
        ShippingMethod shippingMethod,

        @NotNull
        PaymentMethod paymentMethod,

        String promotionCode

) {
}