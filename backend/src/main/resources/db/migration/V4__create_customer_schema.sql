CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    role VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(40) NOT NULL DEFAULT 'PENDING_VERIFICATION',

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_users_role
        CHECK (role IN ('CUSTOMER', 'ADMIN')),

    CONSTRAINT chk_users_status
        CHECK (
            status IN (
                'PENDING_VERIFICATION',
                'ACTIVE',
                'LOCKED',
                'DISABLED'
            )
        )
);

CREATE UNIQUE INDEX ux_users_email_lower
    ON users (LOWER(email));


CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    label VARCHAR(50),

    recipient_name VARCHAR(200) NOT NULL,

    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),

    city VARCHAR(120) NOT NULL,
    state VARCHAR(120) NOT NULL,
    postal_code VARCHAR(30) NOT NULL,
    country_code VARCHAR(2) NOT NULL,

    phone VARCHAR(30),

    default_address BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_addresses_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id
    ON addresses(user_id);

CREATE UNIQUE INDEX ux_addresses_one_default_per_user
    ON addresses(user_id)
    WHERE default_address = TRUE;