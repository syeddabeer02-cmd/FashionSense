package com.fashionsense.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem>
    findByCartIdAndVariantId(
            Long cartId,
            Long variantId
    );

    Optional<CartItem>
    findByIdAndCartId(
            Long id,
            Long cartId
    );

    @Query("""
            SELECT ci
            FROM CartItem ci
            JOIN FETCH ci.variant v
            JOIN FETCH v.product p
            WHERE ci.cart.id = :cartId
            ORDER BY ci.id
            """)
    List<CartItem> findAllWithDetails(
            @Param("cartId") Long cartId
    );
}