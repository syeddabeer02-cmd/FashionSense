package com.fashionsense.wishlist;

import com.fashionsense.catalog.product.Product;
import com.fashionsense.catalog.product.ProductNotFoundException;
import com.fashionsense.catalog.product.ProductRepository;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(
            Long userId
    ) {

        return wishlistRepository
                .findAllWithProductDetails(userId)
                .stream()
                .map(WishlistItemResponse::from)
                .toList();
    }

    @Transactional
    public WishlistItemResponse addProduct(
            Long userId,
            String productSlug
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Authenticated customer not found"
                                )
                        );

        Product product =
                productRepository
                        .findBySlugWithDetails(
                                productSlug
                        )
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with slug: "
                                                + productSlug
                                )
                        );

        Optional<WishlistItem> existing =
                wishlistRepository
                        .findByUserIdAndProductId(
                                userId,
                                product.getId()
                        );

        if (existing.isPresent()) {

            return WishlistItemResponse.from(
                    existing.get()
            );
        }

        WishlistItem item =
                new WishlistItem();

        item.setUser(user);
        item.setProduct(product);

        return WishlistItemResponse.from(
                wishlistRepository.save(item)
        );
    }

    @Transactional
    public void removeProduct(
            Long userId,
            String productSlug
    ) {

        Product product =
                productRepository
                        .findBySlugWithDetails(
                                productSlug
                        )
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with slug: "
                                                + productSlug
                                )
                        );

        wishlistRepository
                .findByUserIdAndProductId(
                        userId,
                        product.getId()
                )
                .ifPresent(
                        wishlistRepository::delete
                );
    }
}