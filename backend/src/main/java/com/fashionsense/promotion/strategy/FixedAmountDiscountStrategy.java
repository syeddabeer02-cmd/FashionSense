package com.fashionsense.promotion.strategy;

import com.fashionsense.promotion.Promotion;
import com.fashionsense.promotion.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FixedAmountDiscountStrategy
        implements PromotionStrategy {

    @Override
    public PromotionType getType() {
        return PromotionType.FIXED_AMOUNT;
    }

    @Override
    public BigDecimal calculateDiscount(
            Promotion promotion,
            BigDecimal unitPrice,
            int quantity
    ) {

        if (promotion == null
                || promotion.getFixedAmount() == null
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
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal discount =
                promotion.getFixedAmount()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        if (discount.signum() <= 0) {
            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        return discount.min(
                merchandiseTotal
        );
    }
}