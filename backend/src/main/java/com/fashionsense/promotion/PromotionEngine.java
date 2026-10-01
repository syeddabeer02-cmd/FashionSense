package com.fashionsense.promotion;

import com.fashionsense.promotion.strategy.PromotionStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PromotionEngine {

    private static final BigDecimal ZERO =
            BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );

    private final PromotionRepository promotionRepository;

    private final Map<
            PromotionType,
            PromotionStrategy
            > strategies;

    public PromotionEngine(
            PromotionRepository promotionRepository,
            List<PromotionStrategy> promotionStrategies
    ) {

        this.promotionRepository =
                promotionRepository;

        this.strategies =
                new EnumMap<>(
                        PromotionType.class
                );

        for (PromotionStrategy strategy
                : promotionStrategies) {

            PromotionStrategy previous =
                    strategies.put(
                            strategy.getType(),
                            strategy
                    );

            if (previous != null) {
                throw new IllegalStateException(
                        "Multiple promotion strategies registered for "
                                + strategy.getType()
                );
            }
        }
    }

    public BigDecimal calculateBestProductDiscount(
            Long productId,
            BigDecimal unitPrice,
            int quantity
    ) {

        if (productId == null
                || unitPrice == null
                || unitPrice.signum() <= 0
                || quantity <= 0) {

            return ZERO;
        }

        BigDecimal merchandiseTotal =
                money(
                        unitPrice.multiply(
                                BigDecimal.valueOf(
                                        quantity
                                )
                        )
                );

        List<Promotion> promotions =
                promotionRepository
                        .findByProductIdAndScopeAndActiveTrue(
                                productId,
                                PromotionScope.PRODUCT
                        );

        BigDecimal bestDiscount =
                ZERO;

        LocalDateTime now =
                LocalDateTime.now();

        for (Promotion promotion
                : promotions) {

            if (!isEligible(
                    promotion,
                    merchandiseTotal,
                    now
            )) {
                continue;
            }

            BigDecimal discount =
                    calculateDiscount(
                            promotion,
                            unitPrice,
                            quantity
                    );

            if (discount.compareTo(
                    bestDiscount
            ) > 0) {

                bestDiscount =
                        discount;
            }
        }

        return bestDiscount.min(
                merchandiseTotal
        );
    }

    public BigDecimal calculateCartDiscount(
            String promotionCode,
            BigDecimal cartSubtotal
    ) {

        if (promotionCode == null
                || promotionCode.isBlank()) {

            return ZERO;
        }

        if (cartSubtotal == null
                || cartSubtotal.signum() <= 0) {

            return ZERO;
        }

        Promotion promotion =
                promotionRepository
                        .findByCodeIgnoreCaseAndScopeAndActiveTrue(
                                promotionCode.trim(),
                                PromotionScope.CART
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Invalid or inactive promotion code."
                                        )
                        );

        BigDecimal normalizedSubtotal =
                money(cartSubtotal);

        if (!isEligible(
                promotion,
                normalizedSubtotal,
                LocalDateTime.now()
        )) {

            throw new IllegalArgumentException(
                    "Promotion code is not currently eligible for this cart."
            );
        }

        BigDecimal discount =
                calculateDiscount(
                        promotion,
                        normalizedSubtotal,
                        1
                );

        return discount.min(
                normalizedSubtotal
        );
    }

    private BigDecimal calculateDiscount(
            Promotion promotion,
            BigDecimal unitPrice,
            int quantity
    ) {

        PromotionStrategy strategy =
                strategies.get(
                        promotion.getType()
                );

        if (strategy == null) {
            throw new IllegalStateException(
                    "No promotion strategy registered for "
                            + promotion.getType()
            );
        }

        BigDecimal discount =
                strategy.calculateDiscount(
                        promotion,
                        unitPrice,
                        quantity
                );

        if (discount == null
                || discount.signum() <= 0) {

            return ZERO;
        }

        return money(discount);
    }

    private boolean isEligible(
            Promotion promotion,
            BigDecimal subtotal,
            LocalDateTime now
    ) {

        if (promotion == null
                || !promotion.isActive()) {

            return false;
        }

        if (promotion.getStartsAt() != null
                && now.isBefore(
                        promotion.getStartsAt()
                )) {

            return false;
        }

        if (promotion.getEndsAt() != null
                && !now.isBefore(
                        promotion.getEndsAt()
                )) {

            return false;
        }

        BigDecimal minimumSubtotal =
                promotion.getMinimumSubtotal();

        return minimumSubtotal == null
                || subtotal.compareTo(
                        minimumSubtotal
                ) >= 0;
    }

    private BigDecimal money(
            BigDecimal amount
    ) {

        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}