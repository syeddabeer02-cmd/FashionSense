package com.fashionsense.catalog.occasion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OccasionService {

    private final OccasionRepository occasionRepository;

    public OccasionService(OccasionRepository occasionRepository) {
        this.occasionRepository = occasionRepository;
    }

    @Transactional(readOnly = true)
    public List<Occasion> getAllOccasions() {
        return occasionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Occasion getOccasionBySlug(String slug) {
        return occasionRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new OccasionNotFoundException(
                                "Occasion not found with slug: " + slug
                        )
                );
    }
}