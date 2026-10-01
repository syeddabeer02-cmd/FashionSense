package com.fashionsense.order;

import com.fashionsense.cart.Cart;
import com.fashionsense.cart.CartItem;
import com.fashionsense.cart.CartItemRepository;
import com.fashionsense.cart.CartItemUnavailableException;
import com.fashionsense.cart.CartRepository;
import com.fashionsense.catalog.variant.ProductVariant;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import com.fashionsense.customer.address.Address;
import com.fashionsense.customer.address.AddressNotFoundException;
import com.fashionsense.customer.address.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutService {

    private static final BigDecimal STANDARD_SHIPPING =
            new BigDecimal("5.99");

    private static final BigDecimal EXPRESS_SHIPPING =
            new BigDecimal("14.99");

    private static final BigDecimal FREE_STANDARD_THRESHOLD =
            new BigDecimal("75.00");

    private static final BigDecimal TAX_RATE =
            new BigDecimal("0.0825");

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderResponse checkout(
            Long userId,
            CheckoutRequest request
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Authenticated customer not found"
                                )
                        );

        Address address =
                addressRepository
                        .findByIdAndUserId(
                                request.addressId(),
                                userId
                        )
                        .orElseThrow(() ->
                                new AddressNotFoundException(
                                        "Shipping address not found"
                                )
                        );

        Cart cart =
                cartRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new EmptyCartException(
                                        "Cart is empty"
                                )
                        );

        List<CartItem> cartItems =
                cartItemRepository
                        .findAllWithDetails(
                                cart.getId()
                        );

        if (cartItems.isEmpty()) {
            throw new EmptyCartException(
                    "Cart is empty"
            );
        }

        BigDecimal subtotal =
                BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            ProductVariant variant =
                    cartItem.getVariant();

            if (!variant.isActive()
                    || !variant.getProduct().isActive()) {

                throw new CartItemUnavailableException(
                        "A product in the cart is currently unavailable"
                );
            }

            if (variant.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new CartItemUnavailableException(
                        "Requested quantity is not available"
                );
            }

            BigDecimal unitPrice =
                    getEffectivePrice(variant);

            BigDecimal lineTotal =
                    unitPrice.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            subtotal =
                    subtotal.add(lineTotal);
        }

        subtotal =
                subtotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal discountAmount =
                BigDecimal.ZERO.setScale(2);

        BigDecimal giftCardAmount =
                BigDecimal.ZERO.setScale(2);

        BigDecimal taxableSubtotal =
                subtotal.subtract(
                        discountAmount
                );

        BigDecimal shippingAmount =
                calculateShipping(
                        taxableSubtotal,
                        request.shippingMethod()
                );

        BigDecimal taxAmount =
                taxableSubtotal
                        .multiply(TAX_RATE)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal totalAmount =
                taxableSubtotal
                        .add(shippingAmount)
                        .add(taxAmount)
                        .subtract(giftCardAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        CustomerOrder order =
                new CustomerOrder();

        order.setUser(user);

        order.setSourceAddressId(
                address.getId()
        );

        order.setOrderNumber(
                generateOrderNumber()
        );

        order.setStatus(
                OrderStatus.PLACED
        );

        order.setShippingMethod(
                request.shippingMethod()
        );

        order.setPaymentMethod(
                request.paymentMethod()
        );

        order.setPaymentStatus(
                PaymentStatus.PENDING
        );

        order.setSubtotal(subtotal);
        order.setDiscountAmount(
                discountAmount
        );
        order.setGiftCardAmount(
                giftCardAmount
        );
        order.setShippingAmount(
                shippingAmount
        );
        order.setTaxAmount(
                taxAmount
        );
        order.setTotalAmount(
                totalAmount
        );

        copyAddressSnapshot(
                address,
                order
        );

        CustomerOrder savedOrder =
                orderRepository.save(order);

        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            ProductVariant variant =
                    cartItem.getVariant();

            BigDecimal unitPrice =
                    getEffectivePrice(variant);

            BigDecimal lineTotal =
                    unitPrice.multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(
                    savedOrder
            );

            orderItem.setVariant(
                    variant
            );

            orderItem.setSku(
                    variant.getSku()
            );

            orderItem.setProductName(
                    variant.getProduct().getName()
            );

            orderItem.setSize(
                    variant.getSize()
            );

            orderItem.setColor(
                    variant.getColor()
            );

            orderItem.setStyle(
                    variant.getStyle()
            );

            orderItem.setMaterial(
                    variant.getMaterial()
            );

            orderItem.setUnitPrice(
                    unitPrice
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setLineTotal(
                    lineTotal
            );

            orderItems.add(orderItem);

            variant.setStockQuantity(
                    variant.getStockQuantity()
                            - cartItem.getQuantity()
            );
        }

        List<OrderItem> savedItems =
                orderItemRepository
                        .saveAll(orderItems);

        /*
         * V1 payment simulation:
         * CARD and PAYPAL both succeed.
         *
         * Later this block can be replaced by
         * Stripe/PayPal/provider adapters.
         */
        savedOrder.setPaymentStatus(
                PaymentStatus.SUCCEEDED
        );

        savedOrder.setStatus(
                OrderStatus.CONFIRMED
        );

        orderRepository.save(savedOrder);

        cartItemRepository.deleteAll(
                cartItems
        );

        cartItemRepository.flush();

        return OrderResponse.from(
                savedOrder,
                savedItems
        );
    }

    private BigDecimal calculateShipping(
            BigDecimal subtotalAfterDiscounts,
            ShippingMethod shippingMethod
    ) {

        if (shippingMethod
                == ShippingMethod.EXPRESS) {

            return EXPRESS_SHIPPING;
        }

        if (subtotalAfterDiscounts
                .compareTo(
                        FREE_STANDARD_THRESHOLD
                ) >= 0) {

            return BigDecimal.ZERO
                    .setScale(2);
        }

        return STANDARD_SHIPPING;
    }

    private BigDecimal getEffectivePrice(
            ProductVariant variant
    ) {

        BigDecimal price =
                variant.getPrice() != null
                        ? variant.getPrice()
                        : variant.getProduct()
                                .getBasePrice();

        return price.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private void copyAddressSnapshot(
            Address address,
            CustomerOrder order
    ) {

        order.setRecipientName(
                address.getRecipientName()
        );

        order.setAddressLine1(
                address.getAddressLine1()
        );

        order.setAddressLine2(
                address.getAddressLine2()
        );

        order.setCity(
                address.getCity()
        );

        order.setState(
                address.getState()
        );

        order.setPostalCode(
                address.getPostalCode()
        );

        order.setCountryCode(
                address.getCountryCode()
        );

        order.setPhone(
                address.getPhone()
        );
    }

    private String generateOrderNumber() {

        return "FS-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}