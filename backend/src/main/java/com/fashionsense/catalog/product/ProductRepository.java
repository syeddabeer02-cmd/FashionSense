package com.fashionsense.catalog.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsBySlug(String slug);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            JOIN FETCH p.brand
            JOIN FETCH p.category
            LEFT JOIN FETCH p.occasions
            WHERE p.slug = :slug
            """)
    Optional<Product> findBySlugWithDetails(
            @Param("slug") String slug
    );

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            JOIN FETCH p.brand
            JOIN FETCH p.category
            LEFT JOIN FETCH p.occasions
            ORDER BY p.id
            """)
    List<Product> findAllWithDetails();

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            JOIN FETCH p.brand
            JOIN FETCH p.category
            LEFT JOIN FETCH p.occasions allOccasions
            WHERE p.active = true
            AND EXISTS (
                SELECT 1
                FROM Product filteredProduct
                JOIN filteredProduct.occasions occasion
                WHERE filteredProduct.id = p.id
                AND occasion.slug = :occasionSlug
            )
            ORDER BY p.id
            """)
    List<Product> findByOccasionSlug(
            @Param("occasionSlug") String occasionSlug
    );
}