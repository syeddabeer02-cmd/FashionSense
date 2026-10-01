package com.fashionsense.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository
        extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem>
    findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    @Query("""
            SELECT DISTINCT w
            FROM WishlistItem w
            JOIN FETCH w.product p
            JOIN FETCH p.brand
            JOIN FETCH p.category
            LEFT JOIN FETCH p.occasions
            WHERE w.user.id = :userId
            ORDER BY w.createdAt DESC
            """)
    List<WishlistItem> findAllWithProductDetails(
            @Param("userId") Long userId
    );
}