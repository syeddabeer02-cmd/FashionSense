package com.fashionsense.cart;

import com.fashionsense.catalog.variant.ProductVariant;
import com.fashionsense.catalog.variant.ProductVariantNotFoundException;
import com.fashionsense.catalog.variant.ProductVariantRepository;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductVariantRepository productVariantRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Transactional
    public CartResponse getCart(
            Long userId
    ) {

        Cart cart =
                getOrCreateCart(userId);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(
            Long userId,
            AddCartItemRequest request
    ) {

        Cart cart =
                getOrCreateCart(userId);

        ProductVariant variant =
                productVariantRepository
                        .findBySku(request.sku())
                        .orElseThrow(() ->
                                new ProductVariantNotFoundException(
                                        "Product variant not found with SKU: "
                                                + request.sku()
                                )
                        );

        if (!variant.isActive()
                || !variant.getProduct().isActive()) {

            throw new CartItemUnavailableException(
                    "Product is currently unavailable"
            );
        }

        CartItem item =
                cartItemRepository
                        .findByCartIdAndVariantId(
                                cart.getId(),
                                variant.getId()
                        )
                        .orElse(null);

        int newQuantity =
                item == null
                        ? request.quantity()
                        : item.getQuantity()
                        + request.quantity();

        validateStock(
                variant,
                newQuantity
        );

        if (item == null) {

            item = new CartItem();

            item.setCart(cart);
            item.setVariant(variant);
        }

        item.setQuantity(newQuantity);

        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateQuantity(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequest request
    ) {

        Cart cart =
                cartRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found"
                                )
                        );

        CartItem item =
                cartItemRepository
                        .findByIdAndCartId(
                                cartItemId,
                                cart.getId()
                        )
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found with id: "
                                                + cartItemId
                                )
                        );

        ProductVariant variant =
                item.getVariant();

        if (!variant.isActive()
                || !variant.getProduct().isActive()) {

            throw new CartItemUnavailableException(
                    "Product is currently unavailable"
            );
        }

        validateStock(
                variant,
                request.quantity()
        );

        item.setQuantity(
                request.quantity()
        );

        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(
            Long userId,
            Long cartItemId
    ) {

        Cart cart =
                cartRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found"
                                )
                        );

        CartItem item =
                cartItemRepository
                        .findByIdAndCartId(
                                cartItemId,
                                cart.getId()
                        )
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found with id: "
                                                + cartItemId
                                )
                        );

        cartItemRepository.delete(item);
        cartItemRepository.flush();

        return buildCartResponse(cart);
    }

    private Cart getOrCreateCart(
            Long userId
    ) {

        return cartRepository
                .findByUserId(userId)
                .orElseGet(() -> {

                    User user =
                            userRepository
                                    .findById(userId)
                                    .orElseThrow(() ->
                                            new IllegalStateException(
                                                    "Authenticated customer not found"
                                            )
                                    );

                    Cart cart = new Cart();

                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    private void validateStock(
            ProductVariant variant,
            int requestedQuantity
    ) {

        if (variant.getStockQuantity()
                < requestedQuantity) {

            throw new CartItemUnavailableException(
                    "Requested quantity is not available"
            );
        }
    }

    private CartResponse buildCartResponse(
            Cart cart
    ) {

        return CartResponse.from(
                cart,
                cartItemRepository
                        .findAllWithDetails(
                                cart.getId()
                        )
        );
    }
}