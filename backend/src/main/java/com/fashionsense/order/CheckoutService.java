package com.fashionsense.order;

import com.fashionsense.cart.Cart;
import com.fashionsense.cart.CartItem;
import com.fashionsense.cart.CartItemRepository;
import com.fashionsense.cart.CartItemUnavailableException;
import com.fashionsense.cart.CartRepository;
import com.fashionsense.catalog.variant.ProductVariant;
import com.fashionsense.catalog.variant.ProductVariantRepository;
import com.fashionsense.config.ProductCacheService;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import com.fashionsense.customer.address.Address;
import com.fashionsense.customer.address.AddressNotFoundException;
import com.fashionsense.customer.address.AddressRepository;
import com.fashionsense.promotion.PromotionEngine;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private static final BigDecimal ZERO =
            BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductCacheService productCacheService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PromotionEngine promotionEngine;

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductVariantRepository productVariantRepository,
            ProductCacheService productCacheService,
            ApplicationEventPublisher applicationEventPublisher,
            PromotionEngine promotionEngine
    ) {
        this.cartRepository =
                cartRepository;

        this.cartItemRepository =
                cartItemRepository;

        this.userRepository =
                userRepository;

        this.addressRepository =
                addressRepository;

        this.orderRepository =
                orderRepository;

        this.orderItemRepository =
                orderItemRepository;

        this.productVariantRepository =
                productVariantRepository;

        this.productCacheService =
                productCacheService;

        this.applicationEventPublisher =
                applicationEventPublisher;

        this.promotionEngine =
                promotionEngine;
    }

    @Transactional
    public OrderResponse checkout(
            Long userId,
            CheckoutRequest request,
            String idempotencyKey
    ) {
        String normalizedIdempotencyKey =
                idempotencyKey.trim();

        /*
         * Serialize checkout attempts that use the same
         * customer + idempotency-key combination.
         *
         * PostgreSQL holds this advisory lock until the
         * current transaction commits or rolls back.
         *
         * This works across multiple application instances
         * because the lock lives in PostgreSQL rather than
         * application memory.
         */
        orderRepository
                .acquireCheckoutIdempotencyLock(
                        userId,
                        normalizedIdempotencyKey
                );

        /*
         * If this request is a retry of a checkout that
         * already completed, return the original order.
         *
         * This check intentionally happens BEFORE reading
         * the cart. A successful first checkout empties the
         * cart, but a retry must still return the previously
         * created order instead of failing with "Cart is empty".
         */
        Optional<CustomerOrder> existingOrder =
                orderRepository
                        .findByUserIdAndIdempotencyKey(
                                userId,
                                normalizedIdempotencyKey
                        );

        if (existingOrder.isPresent()) {

            CustomerOrder order =
                    existingOrder.get();

            List<OrderItem> existingItems =
                    orderItemRepository
                            .findByOrderIdOrderByIdAsc(
                                    order.getId()
                            );

            return OrderResponse.from(
                    order,
                    existingItems
            );
        }

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
                ZERO;

        BigDecimal productDiscountAmount =
                ZERO;

        for (CartItem cartItem : cartItems) {

            ProductVariant variant =
                    cartItem.getVariant();

            if (!variant.isActive()
                    || !variant.getProduct().isActive()) {

                throw new CartItemUnavailableException(
                        "A product in the cart is currently unavailable"
                );
            }

            /*
             * Early availability check for fast customer
             * feedback.
             *
             * This is NOT the final concurrency guarantee.
             * PostgreSQL performs the authoritative atomic
             * stock reservation later in this transaction.
             */
            if (variant.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new CartItemUnavailableException(
                        "Requested quantity is not available"
                );
            }

            BigDecimal unitPrice =
                    getEffectivePrice(
                            variant
                    );

            BigDecimal lineTotal =
                    money(
                            unitPrice.multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
                    );

            subtotal =
                    subtotal.add(
                            lineTotal
                    );

            BigDecimal lineDiscount =
                    promotionEngine
                            .calculateBestProductDiscount(
                                    variant
                                            .getProduct()
                                            .getId(),
                                    unitPrice,
                                    cartItem.getQuantity()
                            );

            if (lineDiscount.compareTo(
                    lineTotal
            ) > 0) {
                lineDiscount =
                        lineTotal;
            }

            productDiscountAmount =
                    productDiscountAmount.add(
                            lineDiscount
                    );
        }

        subtotal =
                money(subtotal);

        productDiscountAmount =
                money(
                        productDiscountAmount
                );

        if (productDiscountAmount
                .compareTo(subtotal) > 0) {

            productDiscountAmount =
                    subtotal;
        }

        BigDecimal subtotalAfterProductDiscounts =
                money(
                        subtotal.subtract(
                                productDiscountAmount
                        )
                );

        if (subtotalAfterProductDiscounts
                .signum() < 0) {

            subtotalAfterProductDiscounts =
                    ZERO;
        }

        BigDecimal cartDiscountAmount =
                promotionEngine
                        .calculateCartDiscount(
                                request.promotionCode(),
                                subtotalAfterProductDiscounts
                        );

        cartDiscountAmount =
                money(
                        cartDiscountAmount
                );

        if (cartDiscountAmount.compareTo(
                subtotalAfterProductDiscounts
        ) > 0) {

            cartDiscountAmount =
                    subtotalAfterProductDiscounts;
        }

        BigDecimal discountAmount =
                money(
                        productDiscountAmount.add(
                                cartDiscountAmount
                        )
                );

        if (discountAmount.compareTo(
                subtotal
        ) > 0) {

            discountAmount =
                    subtotal;
        }

        BigDecimal taxableSubtotal =
                money(
                        subtotal.subtract(
                                discountAmount
                        )
                );

        if (taxableSubtotal.signum() < 0) {
            taxableSubtotal =
                    ZERO;
        }

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

        /*
         * Persist the key on the order itself.
         *
         * V11 also adds a UNIQUE constraint on
         * (user_id, idempotency_key), giving us a second
         * database-level invariant in addition to the
         * advisory transaction lock.
         */
        order.setIdempotencyKey(
                normalizedIdempotencyKey
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

        order.setSubtotal(
                subtotal
        );

        order.setDiscountAmount(
                discountAmount
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
                orderRepository.save(
                        order
                );

        List<OrderItem> orderItems =
                new ArrayList<>();

        Set<String> affectedProductSlugs =
                new HashSet<>();

        for (CartItem cartItem : cartItems) {

            ProductVariant variant =
                    cartItem.getVariant();

            /*
             * Authoritative inventory reservation.
             *
             * PostgreSQL performs:
             *
             * stock = stock - requested quantity
             *
             * only when:
             *
             * stock >= requested quantity.
             *
             * This is one atomic database statement.
             * If another checkout consumes the stock first,
             * this update affects zero rows.
             *
             * Throwing the exception causes the entire
             * @Transactional checkout to roll back,
             * including any stock already reserved for
             * earlier cart items and the new order.
             */
            int updatedRows =
                    productVariantRepository
                            .decrementStockIfAvailable(
                                    variant.getId(),
                                    cartItem.getQuantity()
                            );

            if (updatedRows != 1) {
                throw new CartItemUnavailableException(
                        "Requested quantity is no longer available"
                );
            }

            BigDecimal unitPrice =
                    getEffectivePrice(
                            variant
                    );

            BigDecimal lineTotal =
                    money(
                            unitPrice.multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
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
                    variant
                            .getProduct()
                            .getName()
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

            orderItems.add(
                    orderItem
            );

            affectedProductSlugs.add(
                    variant
                            .getProduct()
                            .getSlug()
            );
        }

        List<OrderItem> savedItems =
                orderItemRepository
                        .saveAll(
                                orderItems
                        );

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

        orderRepository.save(
                savedOrder
        );

        cartItemRepository.deleteAll(
                cartItems
        );

        cartItemRepository.flush();

        for (String productSlug
                : affectedProductSlugs) {

            productCacheService
                    .evictProductDetailAfterCommit(
                            productSlug
                    );
        }

        applicationEventPublisher
                .publishEvent(
                        new OrderConfirmedEvent(
                                savedOrder
                                        .getOrderNumber(),
                                userId,
                                savedOrder
                                        .getTotalAmount(),
                                savedOrder
                                        .getShippingMethod()
                                        .name(),
                                savedOrder
                                        .getPaymentMethod()
                                        .name(),
                                Instant.now()
                        )
                );

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

            return ZERO;
        }

        return STANDARD_SHIPPING;
    }

    private BigDecimal getEffectivePrice(
            ProductVariant variant
    ) {
        BigDecimal price =
                variant.getPrice() != null
                        ? variant.getPrice()
                        : variant
                                .getProduct()
                                .getBasePrice();

        return money(
                price
        );
    }

    private BigDecimal money(
            BigDecimal amount
    ) {
        return amount.setScale(
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