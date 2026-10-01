package com.fashionsense.promotion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository
        extends JpaRepository<Promotion, Long> {

    List<Promotion>
    findByProductIdAndScopeAndActiveTrue(
            Long productId,
            PromotionScope scope
    );

    Optional<Promotion>
    findByCodeIgnoreCaseAndScopeAndActiveTrue(
            String code,
            PromotionScope scope
    );

    boolean existsByCodeIgnoreCase(
            String code
    );
}