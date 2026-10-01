package com.fashionsense.catalog.occasion;

public record OccasionResponse(
        Long id,
        String name,
        String slug
) {

    public static OccasionResponse from(Occasion occasion) {
        return new OccasionResponse(
                occasion.getId(),
                occasion.getName(),
                occasion.getSlug()
        );
    }
}