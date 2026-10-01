package com.fashionsense.promotion;

import com.fashionsense.catalog.product.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromotionScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PromotionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(
            name = "fixed_amount",
            precision = 12,
            scale = 2
    )
    private BigDecimal fixedAmount;

    @Column(name = "buy_quantity")
    private Integer buyQuantity;

    @Column(name = "get_quantity")
    private Integer getQuantity;

    @Column(
            name = "minimum_subtotal",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal minimumSubtotal =
            BigDecimal.ZERO;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public Promotion() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        normalizeCode();

        if (minimumSubtotal == null) {
            minimumSubtotal =
                    BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();

        normalizeCode();
    }

    private void normalizeCode() {

        if (code == null) {
            return;
        }

        String normalized =
                code.trim();

        code =
                normalized.isEmpty()
                        ? null
                        : normalized.toUpperCase(
                                Locale.ROOT
                        );
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(
            String code
    ) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public PromotionScope getScope() {
        return scope;
    }

    public void setScope(
            PromotionScope scope
    ) {
        this.scope = scope;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(
            PromotionType type
    ) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(
            Product product
    ) {
        this.product = product;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(
            BigDecimal percentage
    ) {
        this.percentage = percentage;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(
            BigDecimal fixedAmount
    ) {
        this.fixedAmount = fixedAmount;
    }

    public Integer getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(
            Integer buyQuantity
    ) {
        this.buyQuantity = buyQuantity;
    }

    public Integer getGetQuantity() {
        return getQuantity;
    }

    public void setGetQuantity(
            Integer getQuantity
    ) {
        this.getQuantity = getQuantity;
    }

    public BigDecimal getMinimumSubtotal() {
        return minimumSubtotal;
    }

    public void setMinimumSubtotal(
            BigDecimal minimumSubtotal
    ) {
        this.minimumSubtotal =
                minimumSubtotal;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(
            LocalDateTime startsAt
    ) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(
            LocalDateTime endsAt
    ) {
        this.endsAt = endsAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}