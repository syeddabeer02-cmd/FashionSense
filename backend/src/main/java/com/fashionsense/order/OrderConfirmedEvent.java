package com.fashionsense.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderConfirmedEvent(
        String orderNumber,
        Long userId,
        BigDecimal totalAmount,
        String shippingMethod,
        String paymentMethod,
        Instant occurredAt
) {
}