CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    source_address_id BIGINT,

    order_number VARCHAR(50) NOT NULL UNIQUE,

    status VARCHAR(30) NOT NULL,
    shipping_method VARCHAR(30) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) NOT NULL,

    subtotal NUMERIC(12,2) NOT NULL,
    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    gift_card_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    shipping_amount NUMERIC(12,2) NOT NULL,
    tax_amount NUMERIC(12,2) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,

    recipient_name VARCHAR(200) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(120) NOT NULL,
    state VARCHAR(120) NOT NULL,
    postal_code VARCHAR(30) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    phone VARCHAR(30),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_orders_source_address
        FOREIGN KEY (source_address_id)
        REFERENCES addresses(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_orders_status
        CHECK (
            status IN (
                'PLACED',
                'CONFIRMED',
                'PACKED',
                'SHIPPED',
                'DELIVERED',
                'CANCELLED',
                'FAILED'
            )
        ),

    CONSTRAINT chk_orders_shipping_method
        CHECK (
            shipping_method IN (
                'STANDARD',
                'EXPRESS'
            )
        ),

    CONSTRAINT chk_orders_payment_method
        CHECK (
            payment_method IN (
                'CARD',
                'PAYPAL'
            )
        ),

    CONSTRAINT chk_orders_payment_status
        CHECK (
            payment_status IN (
                'PENDING',
                'SUCCEEDED',
                'FAILED'
            )
        ),

    CONSTRAINT chk_orders_money
        CHECK (
            subtotal >= 0
            AND discount_amount >= 0
            AND gift_card_amount >= 0
            AND shipping_amount >= 0
            AND tax_amount >= 0
            AND total_amount >= 0
        )
);


CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,
    variant_id BIGINT,

    sku VARCHAR(120) NOT NULL,
    product_name VARCHAR(200) NOT NULL,

    size VARCHAR(100),
    color VARCHAR(100),
    style VARCHAR(100),
    material VARCHAR(100),

    unit_price NUMERIC(12,2) NOT NULL,
    quantity INTEGER NOT NULL,
    line_total NUMERIC(12,2) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_items_variant
        FOREIGN KEY (variant_id)
        REFERENCES product_variants(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_order_items_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_order_items_money
        CHECK (
            unit_price >= 0
            AND line_total >= 0
        )
);

CREATE INDEX idx_orders_user_id
    ON orders(user_id);

CREATE INDEX idx_orders_status
    ON orders(status);

CREATE INDEX idx_orders_created_at
    ON orders(created_at);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);