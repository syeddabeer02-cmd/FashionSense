package com.fashionsense.catalog.occasion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OccasionRepository extends JpaRepository<Occasion, Long> {

    Optional<Occasion> findBySlug(String slug);
}