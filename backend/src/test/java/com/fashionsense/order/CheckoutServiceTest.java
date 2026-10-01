package com.fashionsense.order;

import com.fashionsense.cart.Cart;
import com.fashionsense.cart.CartItem;
import com.fashionsense.cart.CartItemRepository;
import com.fashionsense.cart.CartItemUnavailableException;
import com.fashionsense.cart.CartRepository;
import com.fashionsense.catalog.product.Product;
import com.fashionsense.catalog.variant.ProductVariant;
import com.fashionsense.config.ProductCacheService;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import com.fashionsense.customer.address.Address;
import com.fashionsense.customer.address.AddressRepository;
import com.fashionsense.promotion.PromotionEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    private static final Long USER_ID =
            1L;

    private static final Long ADDRESS_ID =
            100L;

    private static final Long CART_ID =
            300L;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductCacheService productCacheService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private PromotionEngine promotionEngine;

    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {

        lenient()
                .when(
                        promotionEngine
                                .calculateBestProductDiscount(
                                        nullable(Long.class),
                                        any(BigDecimal.class),
                                        anyInt()
                                )
                )
                .thenReturn(
                        new BigDecimal("0.00")
                );

        lenient()
                .when(
                        promotionEngine
                                .calculateCartDiscount(
                                        nullable(String.class),
                                        any(BigDecimal.class)
                                )
                )
                .thenReturn(
                        new BigDecimal("0.00")
                );

        checkoutService =
                new CheckoutService(
                        cartRepository,
                        cartItemRepository,
                        userRepository,
                        addressRepository,
                        orderRepository,
                        orderItemRepository,
                        productCacheService,
                        applicationEventPublisher,
                        promotionEngine
                );
    }

    @Test
    void standardCheckoutOverThresholdUsesFreeShippingAndCalculatesTax() {

        TestData testData =
                prepareCheckout(
                        new BigDecimal("79.99"),
                        10,
                        1
                );

        stubSuccessfulPersistence();

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.STANDARD,
                        PaymentMethod.CARD,
                        null
                );

        OrderResponse response =
                checkoutService.checkout(
                        USER_ID,
                        request
                );

        assertEquals(
                new BigDecimal("79.99"),
                response.subtotal()
        );

        assertEquals(
                new BigDecimal("0.00"),
                response.discountAmount()
        );

        assertEquals(
                new BigDecimal("0.00"),
                response.shippingAmount()
        );

        assertEquals(
                new BigDecimal("6.60"),
                response.taxAmount()
        );

        assertEquals(
                new BigDecimal("86.59"),
                response.totalAmount()
        );

        assertEquals(
                "CONFIRMED",
                response.status()
        );

        assertEquals(
                "SUCCEEDED",
                response.paymentStatus()
        );

        assertEquals(
                "STANDARD",
                response.shippingMethod()
        );

        assertEquals(
                "CARD",
                response.paymentMethod()
        );

        assertTrue(
                response.orderNumber()
                        .startsWith("FS-")
        );

        assertEquals(
                9,
                testData.variant()
                        .getStockQuantity()
        );

        assertEquals(
                1,
                response.items().size()
        );

        assertEquals(
                "TEST-SKU-001",
                response.items()
                        .getFirst()
                        .sku()
        );

        assertEquals(
                "Test Hoodie",
                response.items()
                        .getFirst()
                        .productName()
        );

        assertEquals(
                new BigDecimal("79.99"),
                response.items()
                        .getFirst()
                        .unitPrice()
        );

        verify(cartItemRepository)
                .deleteAll(
                        List.of(
                                testData.cartItem()
                        )
                );

        verify(cartItemRepository)
                .flush();

        verify(orderRepository, times(2))
                .save(
                        any(CustomerOrder.class)
                );

        verify(orderItemRepository)
                .saveAll(
                        anyList()
                );

        verify(productCacheService)
                .evictProductDetailAfterCommit(
                        "test-hoodie"
                );

        verify(applicationEventPublisher)
                .publishEvent(
                        org.mockito.ArgumentMatchers
                                .<Object>argThat(
                                        event ->
                                                event
                                                        instanceof OrderConfirmedEvent confirmedEvent
                                                        && USER_ID.equals(
                                                        confirmedEvent.userId()
                                                )
                                                        && response
                                                        .orderNumber()
                                                        .equals(
                                                                confirmedEvent
                                                                        .orderNumber()
                                                        )
                                                        && new BigDecimal(
                                                        "86.59"
                                                ).compareTo(
                                                        confirmedEvent
                                                                .totalAmount()
                                                ) == 0
                                                        && "STANDARD".equals(
                                                        confirmedEvent
                                                                .shippingMethod()
                                                )
                                                        && "CARD".equals(
                                                        confirmedEvent
                                                                .paymentMethod()
                                                )
                                                        && confirmedEvent
                                                        .occurredAt()
                                                        != null
                                )
                );
    }

    @Test
    void expressCheckoutAlwaysChargesExpressShipping() {

        prepareCheckout(
                new BigDecimal("79.99"),
                10,
                1
        );

        stubSuccessfulPersistence();

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.EXPRESS,
                        PaymentMethod.PAYPAL,
                        null
                );

        OrderResponse response =
                checkoutService.checkout(
                        USER_ID,
                        request
                );

        assertEquals(
                new BigDecimal("79.99"),
                response.subtotal()
        );

        assertEquals(
                new BigDecimal("0.00"),
                response.discountAmount()
        );

        assertEquals(
                new BigDecimal("14.99"),
                response.shippingAmount()
        );

        assertEquals(
                new BigDecimal("6.60"),
                response.taxAmount()
        );

        assertEquals(
                new BigDecimal("101.58"),
                response.totalAmount()
        );

        assertEquals(
                "EXPRESS",
                response.shippingMethod()
        );

        assertEquals(
                "PAYPAL",
                response.paymentMethod()
        );

        assertEquals(
                "CONFIRMED",
                response.status()
        );

        assertEquals(
                "SUCCEEDED",
                response.paymentStatus()
        );

        verify(productCacheService)
                .evictProductDetailAfterCommit(
                        "test-hoodie"
                );

        verify(applicationEventPublisher)
                .publishEvent(
                        any(
                                OrderConfirmedEvent.class
                        )
                );
    }

    @Test
    void standardCheckoutBelowThresholdChargesStandardShipping() {

        prepareCheckout(
                new BigDecimal("50.00"),
                10,
                1
        );

        stubSuccessfulPersistence();

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.STANDARD,
                        PaymentMethod.CARD,
                        null
                );

        OrderResponse response =
                checkoutService.checkout(
                        USER_ID,
                        request
                );

        assertEquals(
                new BigDecimal("50.00"),
                response.subtotal()
        );

        assertEquals(
                new BigDecimal("0.00"),
                response.discountAmount()
        );

        assertEquals(
                new BigDecimal("5.99"),
                response.shippingAmount()
        );

        assertEquals(
                new BigDecimal("4.13"),
                response.taxAmount()
        );

        assertEquals(
                new BigDecimal("60.12"),
                response.totalAmount()
        );

        verify(productCacheService)
                .evictProductDetailAfterCommit(
                        "test-hoodie"
                );

        verify(applicationEventPublisher)
                .publishEvent(
                        any(
                                OrderConfirmedEvent.class
                        )
                );
    }

    @Test
    void checkoutAppliesProductAndCartPromotionsBeforeShippingAndTax() {

        prepareCheckout(
                new BigDecimal("79.99"),
                10,
                1
        );

        when(
                promotionEngine
                        .calculateBestProductDiscount(
                                nullable(Long.class),
                                any(BigDecimal.class),
                                anyInt()
                        )
        ).thenReturn(
                new BigDecimal("10.00")
        );

        when(
                promotionEngine
                        .calculateCartDiscount(
                                "SAVE5",
                                new BigDecimal("69.99")
                        )
        ).thenReturn(
                new BigDecimal("5.00")
        );

        stubSuccessfulPersistence();

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.STANDARD,
                        PaymentMethod.CARD,
                        "SAVE5"
                );

        OrderResponse response =
                checkoutService.checkout(
                        USER_ID,
                        request
                );

        assertEquals(
                new BigDecimal("79.99"),
                response.subtotal()
        );

        assertEquals(
                new BigDecimal("15.00"),
                response.discountAmount()
        );

        /*
         * 79.99
         * - 10.00 product discount
         * -  5.00 cart promotion
         * = 64.99 discounted merchandise subtotal
         *
         * 64.99 is below the $75 free
         * standard-shipping threshold.
         */
        assertEquals(
                new BigDecimal("5.99"),
                response.shippingAmount()
        );

        /*
         * Tax:
         * 64.99 * 8.25% = 5.36
         */
        assertEquals(
                new BigDecimal("5.36"),
                response.taxAmount()
        );

        /*
         * 64.99
         * + 5.99 shipping
         * + 5.36 tax
         * = 76.34
         */
        assertEquals(
                new BigDecimal("76.34"),
                response.totalAmount()
        );

        assertEquals(
                "CONFIRMED",
                response.status()
        );

        assertEquals(
                "SUCCEEDED",
                response.paymentStatus()
        );

        verify(promotionEngine)
                .calculateCartDiscount(
                        "SAVE5",
                        new BigDecimal("69.99")
                );

        verify(applicationEventPublisher)
                .publishEvent(
                        org.mockito.ArgumentMatchers
                                .<Object>argThat(
                                        event ->
                                                event
                                                        instanceof OrderConfirmedEvent confirmedEvent
                                                        && new BigDecimal(
                                                        "76.34"
                                                ).compareTo(
                                                        confirmedEvent
                                                                .totalAmount()
                                                ) == 0
                                )
                );
    }

    @Test
    void checkoutRejectsInsufficientInventory() {

        prepareCheckout(
                new BigDecimal("79.99"),
                1,
                2
        );

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.STANDARD,
                        PaymentMethod.CARD,
                        null
                );

        CartItemUnavailableException exception =
                assertThrows(
                        CartItemUnavailableException.class,
                        () ->
                                checkoutService.checkout(
                                        USER_ID,
                                        request
                                )
                );

        assertEquals(
                "Requested quantity is not available",
                exception.getMessage()
        );

        verifyNoInteractions(
                orderRepository,
                orderItemRepository,
                productCacheService,
                applicationEventPublisher,
                promotionEngine
        );

        verify(cartItemRepository, never())
                .deleteAll(
                        anyList()
                );
    }

    @Test
    void checkoutRejectsInactiveVariant() {

        TestData testData =
                prepareCheckout(
                        new BigDecimal("79.99"),
                        10,
                        1
                );

        testData.variant()
                .setActive(false);

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.STANDARD,
                        PaymentMethod.CARD,
                        null
                );

        CartItemUnavailableException exception =
                assertThrows(
                        CartItemUnavailableException.class,
                        () ->
                                checkoutService.checkout(
                                        USER_ID,
                                        request
                                )
                );

        assertEquals(
                "A product in the cart is currently unavailable",
                exception.getMessage()
        );

        verifyNoInteractions(
                orderRepository,
                orderItemRepository,
                productCacheService,
                applicationEventPublisher,
                promotionEngine
        );

        verify(cartItemRepository, never())
                .deleteAll(
                        anyList()
                );
    }

    @Test
    void checkoutRejectsEmptyCart() {

        User user =
                new User();

        Address address =
                createAddress();

        Cart cart =
                org.mockito.Mockito.mock(
                        Cart.class
                );

        when(cart.getId())
                .thenReturn(
                        CART_ID
                );

        when(
                userRepository
                        .findById(
                                USER_ID
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                addressRepository
                        .findByIdAndUserId(
                                ADDRESS_ID,
                                USER_ID
                        )
        ).thenReturn(
                Optional.of(address)
        );

        when(
                cartRepository
                        .findByUserId(
                                USER_ID
                        )
        ).thenReturn(
                Optional.of(cart)
        );

        when(
                cartItemRepository
                        .findAllWithDetails(
                                CART_ID
                        )
        ).thenReturn(
                List.of()
        );

        CheckoutRequest request =
                new CheckoutRequest(
                        ADDRESS_ID,
                        ShippingMethod.STANDARD,
                        PaymentMethod.CARD,
                        null
                );

        EmptyCartException exception =
                assertThrows(
                        EmptyCartException.class,
                        () ->
                                checkoutService.checkout(
                                        USER_ID,
                                        request
                                )
                );

        assertEquals(
                "Cart is empty",
                exception.getMessage()
        );

        verifyNoInteractions(
                orderRepository,
                orderItemRepository,
                productCacheService,
                applicationEventPublisher,
                promotionEngine
        );
    }

    private TestData prepareCheckout(
            BigDecimal price,
            int stockQuantity,
            int cartQuantity
    ) {

        User user =
                new User();

        Address address =
                createAddress();

        Cart cart =
                org.mockito.Mockito.mock(
                        Cart.class
                );

        when(cart.getId())
                .thenReturn(
                        CART_ID
                );

        Product product =
                new Product();

        product.setName(
                "Test Hoodie"
        );

        product.setSlug(
                "test-hoodie"
        );

        product.setBasePrice(
                price
        );

        product.setActive(
                true
        );

        ProductVariant variant =
                new ProductVariant();

        variant.setProduct(
                product
        );

        variant.setSku(
                "TEST-SKU-001"
        );

        variant.setSize(
                "M"
        );

        variant.setColor(
                "Black"
        );

        variant.setStyle(
                "Casual"
        );

        variant.setMaterial(
                "Cotton"
        );

        variant.setPrice(
                price
        );

        variant.setStockQuantity(
                stockQuantity
        );

        variant.setActive(
                true
        );

        CartItem cartItem =
                new CartItem();

        cartItem.setCart(
                cart
        );

        cartItem.setVariant(
                variant
        );

        cartItem.setQuantity(
                cartQuantity
        );

        when(
                userRepository
                        .findById(
                                USER_ID
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                addressRepository
                        .findByIdAndUserId(
                                ADDRESS_ID,
                                USER_ID
                        )
        ).thenReturn(
                Optional.of(address)
        );

        when(
                cartRepository
                        .findByUserId(
                                USER_ID
                        )
        ).thenReturn(
                Optional.of(cart)
        );

        when(
                cartItemRepository
                        .findAllWithDetails(
                                CART_ID
                        )
        ).thenReturn(
                List.of(
                        cartItem
                )
        );

        return new TestData(
                variant,
                cartItem
        );
    }

    private void stubSuccessfulPersistence() {

        when(
                orderRepository.save(
                        any(
                                CustomerOrder.class
                        )
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(
                                0
                        )
        );

        when(
                orderItemRepository.saveAll(
                        anyList()
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(
                                0
                        )
        );
    }

    private Address createAddress() {

        Address address =
                new Address();

        address.setRecipientName(
                "Test Customer"
        );

        address.setAddressLine1(
                "123 Test Street"
        );

        address.setAddressLine2(
                "Apartment 1"
        );

        address.setCity(
                "Test City"
        );

        address.setState(
                "TX"
        );

        address.setPostalCode(
                "78201"
        );

        address.setCountryCode(
                "US"
        );

        address.setPhone(
                "2105550100"
        );

        return address;
    }

    private record TestData(
            ProductVariant variant,
            CartItem cartItem
    ) {
    }
}