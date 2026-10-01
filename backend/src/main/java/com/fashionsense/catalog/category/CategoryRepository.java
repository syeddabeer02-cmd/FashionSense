package com.fashionsense.catalog.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findByParentIsNull();

    @Query("""
            SELECT c
            FROM Category c
            JOIN FETCH c.parent
            WHERE c.parent.id = :parentId
            """)
    List<Category> findChildrenWithParent(
            @Param("parentId") Long parentId
    );
}