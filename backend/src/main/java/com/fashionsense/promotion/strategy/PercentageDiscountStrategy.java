package com.fashionsense.promotion.strategy;

import com.fashionsense.promotion.Promotion;
import com.fashionsense.promotion.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentageDiscountStrategy
        implements PromotionStrategy {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    @Override
    public PromotionType getType() {
        return PromotionType.PERCENTAGE;
    }

    @Override
    public BigDecimal calculateDiscount(
            Promotion promotion,
            BigDecimal unitPrice,
            int quantity
    ) {

        if (promotion == null
                || promotion.getPercentage() == null
                || unitPrice == null
                || unitPrice.signum() <= 0
                || quantity <= 0) {

            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        BigDecimal merchandiseTotal =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                quantity
                        )
                );

        BigDecimal discount =
                merchandiseTotal
                        .multiply(
                                promotion.getPercentage()
                        )
                        .divide(
                                ONE_HUNDRED,
                                2,
                                RoundingMode.HALF_UP
                        );

        return discount.min(
                merchandiseTotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );
    }
}