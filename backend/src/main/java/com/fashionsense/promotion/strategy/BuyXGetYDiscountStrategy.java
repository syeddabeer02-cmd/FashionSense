package com.fashionsense.promotion.strategy;

import com.fashionsense.promotion.Promotion;
import com.fashionsense.promotion.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BuyXGetYDiscountStrategy
        implements PromotionStrategy {

    @Override
    public PromotionType getType() {
        return PromotionType.BUY_X_GET_Y;
    }

    @Override
    public BigDecimal calculateDiscount(
            Promotion promotion,
            BigDecimal unitPrice,
            int quantity
    ) {

        if (promotion == null
                || promotion.getBuyQuantity() == null
                || promotion.getGetQuantity() == null
                || unitPrice == null
                || unitPrice.signum() <= 0
                || quantity <= 0) {

            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        int buyQuantity =
                promotion.getBuyQuantity();

        int getQuantity =
                promotion.getGetQuantity();

        if (buyQuantity <= 0
                || getQuantity <= 0) {

            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        int groupSize =
                buyQuantity
                        + getQuantity;

        int completeGroups =
                quantity / groupSize;

        int freeUnits =
                completeGroups
                        * getQuantity;

        if (freeUnits <= 0) {
            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        BigDecimal discount =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                freeUnits
                        )
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal merchandiseTotal =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                quantity
                        )
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        return discount.min(
                merchandiseTotal
        );
    }
}