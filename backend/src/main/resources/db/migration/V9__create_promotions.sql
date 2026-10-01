CREATE TABLE promotions (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(50),
    name VARCHAR(150) NOT NULL,

    scope VARCHAR(20) NOT NULL,
    type VARCHAR(30) NOT NULL,

    product_id BIGINT REFERENCES products(id),

    percentage NUMERIC(5, 2),
    fixed_amount NUMERIC(12, 2),

    buy_quantity INTEGER,
    get_quantity INTEGER,

    minimum_subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0.00,

    starts_at TIMESTAMP,
    ends_at TIMESTAMP,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_promotions_scope
        CHECK (scope IN ('PRODUCT', 'CART')),

    CONSTRAINT chk_promotions_type
        CHECK (
            type IN (
                'PERCENTAGE',
                'FIXED_AMOUNT',
                'BUY_X_GET_Y'
            )
        ),

    CONSTRAINT chk_promotions_scope_configuration
        CHECK (
            (
                scope = 'PRODUCT'
                AND product_id IS NOT NULL
                AND code IS NULL
            )
            OR
            (
                scope = 'CART'
                AND product_id IS NULL
                AND code IS NOT NULL
                AND type IN ('PERCENTAGE', 'FIXED_AMOUNT')
            )
        ),

    CONSTRAINT chk_promotions_percentage
        CHECK (
            type <> 'PERCENTAGE'
            OR (
                percentage IS NOT NULL
                AND percentage > 0
                AND percentage <= 100
            )
        ),

    CONSTRAINT chk_promotions_fixed_amount
        CHECK (
            type <> 'FIXED_AMOUNT'
            OR (
                fixed_amount IS NOT NULL
                AND fixed_amount > 0
            )
        ),

    CONSTRAINT chk_promotions_buy_x_get_y
        CHECK (
            type <> 'BUY_X_GET_Y'
            OR (
                scope = 'PRODUCT'
                AND buy_quantity IS NOT NULL
                AND get_quantity IS NOT NULL
                AND buy_quantity > 0
                AND get_quantity > 0
            )
        ),

    CONSTRAINT chk_promotions_minimum_subtotal
        CHECK (minimum_subtotal >= 0),

    CONSTRAINT chk_promotions_date_range
        CHECK (
            starts_at IS NULL
            OR ends_at IS NULL
            OR ends_at > starts_at
        )
);

CREATE UNIQUE INDEX uq_promotions_code_ci
    ON promotions (UPPER(code))
    WHERE code IS NOT NULL;

CREATE INDEX idx_promotions_product_active
    ON promotions (product_id, active)
    WHERE scope = 'PRODUCT';

CREATE INDEX idx_promotions_cart_active
    ON promotions (active)
    WHERE scope = 'CART';