package com.fashionsense.catalog.category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        Long parentId,
        String parentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CategoryResponse from(Category category) {

        Long parentId = null;
        String parentName = null;

        if (category.getParent() != null) {
            parentId = category.getParent().getId();
            parentName = category.getParent().getName();
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                parentId,
                parentName,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}