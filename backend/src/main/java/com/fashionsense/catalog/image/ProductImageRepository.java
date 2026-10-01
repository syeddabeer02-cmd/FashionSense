package com.fashionsense.catalog.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {

    @Query("""
            SELECT i
            FROM ProductImage i
            JOIN FETCH i.product
            WHERE i.product.id = :productId
            ORDER BY i.displayOrder ASC, i.id ASC
            """)
    List<ProductImage> findByProductIdWithProduct(
            @Param("productId") Long productId
    );

    boolean existsByProductIdAndPrimaryImageTrue(Long productId);
}