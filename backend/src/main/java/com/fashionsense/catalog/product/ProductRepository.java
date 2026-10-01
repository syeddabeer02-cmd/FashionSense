package com.fashionsense.catalog.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySlug(String slug);

    @Query("""
            SELECT p
            FROM Product p
            JOIN FETCH p.brand
            JOIN FETCH p.category
            WHERE p.slug = :slug
            """)
    Optional<Product> findBySlugWithDetails(
            @Param("slug") String slug
    );

    @Query("""
            SELECT p
            FROM Product p
            JOIN FETCH p.brand
            JOIN FETCH p.category
            ORDER BY p.id
            """)
    List<Product> findAllWithDetails();
}