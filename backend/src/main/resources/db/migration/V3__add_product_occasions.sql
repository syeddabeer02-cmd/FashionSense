CREATE TABLE occasions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE product_occasions (
    product_id BIGINT NOT NULL,
    occasion_id BIGINT NOT NULL,

    PRIMARY KEY (product_id, occasion_id),

    CONSTRAINT fk_product_occasions_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_product_occasions_occasion
        FOREIGN KEY (occasion_id)
        REFERENCES occasions(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_product_occasions_product_id
    ON product_occasions(product_id);

CREATE INDEX idx_product_occasions_occasion_id
    ON product_occasions(occasion_id);

INSERT INTO occasions (name, slug)
VALUES
    ('Wedding', 'wedding'),
    ('Party', 'party'),
    ('Office', 'office'),
    ('Casual', 'casual'),
    ('Festive', 'festive'),
    ('Date Night', 'date-night'),
    ('Vacation', 'vacation'),
    ('Sports / Activewear', 'sports-activewear'),
    ('Winter', 'winter'),
    ('Summer', 'summer');