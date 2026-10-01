package com.fashionsense.catalog.variant;

import org.springframework.data.jpa.repository.JpaRepository;
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
}