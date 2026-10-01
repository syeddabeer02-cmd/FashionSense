package com.fashionsense.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String status,
        String shippingMethod,
        String paymentMethod,
        String paymentStatus,

        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal giftCardAmount,
        BigDecimal shippingAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,

        String recipientName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String countryCode,
        String phone,

        List<OrderItemResponse> items,

        LocalDateTime createdAt
) {

    public static OrderResponse from(
            CustomerOrder order,
            List<OrderItem> items
    ) {

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getShippingMethod().name(),
                order.getPaymentMethod().name(),
                order.getPaymentStatus().name(),

                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getGiftCardAmount(),
                order.getShippingAmount(),
                order.getTaxAmount(),
                order.getTotalAmount(),

                order.getRecipientName(),
                order.getAddressLine1(),
                order.getAddressLine2(),
                order.getCity(),
                order.getState(),
                order.getPostalCode(),
                order.getCountryCode(),
                order.getPhone(),

                items.stream()
                        .map(OrderItemResponse::from)
                        .toList(),

                order.getCreatedAt()
        );
    }
}