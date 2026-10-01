package com.fashionsense.promotion.strategy;

import com.fashionsense.promotion.Promotion;
import com.fashionsense.promotion.PromotionType;

import java.math.BigDecimal;

public interface PromotionStrategy {

    PromotionType getType();

    BigDecimal calculateDiscount(
            Promotion promotion,
            BigDecimal unitPrice,
            int quantity
    );
}