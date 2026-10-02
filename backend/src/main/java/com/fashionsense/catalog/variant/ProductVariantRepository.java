package com.fashionsense.catalog.variant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    boolean existsBySku(String sku);

    Optional<ProductVariant> findBySku(String sku);

    @Query("""
            SELECT v
            FROM ProductVariant v
            JOIN FETCH v.product
            WHERE v.product.id = :productId
            ORDER BY v.id
            """)
    List<ProductVariant> findByProductIdWithProduct(
            @Param("productId") Long productId
    );

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE ProductVariant v
            SET v.stockQuantity =
                v.stockQuantity - :quantity
            WHERE v.id = :variantId
              AND v.active = true
              AND v.stockQuantity >= :quantity
            """)
    int decrementStockIfAvailable(
            @Param("variantId") Long variantId,
            @Param("quantity") int quantity
    );
}