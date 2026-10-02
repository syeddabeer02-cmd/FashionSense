ALTER TABLE orders
    ADD COLUMN idempotency_key VARCHAR(100);

ALTER TABLE orders
    ADD CONSTRAINT uk_orders_user_idempotency_key
        UNIQUE (user_id, idempotency_key);