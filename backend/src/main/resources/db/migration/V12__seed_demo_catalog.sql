-- Fashion Sense reproducible demo catalog.
-- This migration expands the small development catalog with
-- representative brands, categories, products, variants, images,
-- inventory states, and occasion mappings.

-- ============================================================
-- BRANDS
-- ============================================================

INSERT INTO brands (
    name,
    slug,
    description,
    active
)
VALUES
    (
        'Nike',
        'nike',
        'Sportswear, footwear and performance-inspired everyday fashion.',
        TRUE
    ),
    (
        'Adidas',
        'adidas',
        'Sportswear, footwear and everyday active fashion.',
        TRUE
    ),
    (
        'Levi''s',
        'levis',
        'Denim and casual lifestyle apparel.',
        TRUE
    ),
    (
        'Puma',
        'puma',
        'Sports-inspired apparel and footwear.',
        TRUE
    ),
    (
        'Tommy Hilfiger',
        'tommy-hilfiger',
        'Classic American casual and smart-casual fashion.',
        TRUE
    ),
    (
        'Allen Solly',
        'allen-solly',
        'Smart casual and office-focused apparel.',
        TRUE
    )
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- BASE MEN CATEGORY HIERARCHY
-- ============================================================

INSERT INTO categories (
    name,
    slug,
    parent_id
)
VALUES (
    'Men',
    'men',
    NULL
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO categories (
    name,
    slug,
    parent_id
)
VALUES (
    'Clothing',
    'mens-clothing',
    (
        SELECT id
        FROM categories
        WHERE slug = 'men'
    )
)
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- CATEGORIES
-- ============================================================

INSERT INTO categories (
    name,
    slug,
    parent_id
)
VALUES
    (
        'Footwear',
        'mens-footwear',
        (
            SELECT id
            FROM categories
            WHERE slug = 'men'
        )
    ),
    (
        'Shirts',
        'mens-shirts',
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-clothing'
        )
    ),
    (
        'T-Shirts',
        'mens-tshirts',
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-clothing'
        )
    ),
    (
        'Jeans',
        'mens-jeans',
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-clothing'
        )
    ),
    (
        'Jackets',
        'mens-jackets',
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-clothing'
        )
    )
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- PRODUCTS
-- ============================================================

INSERT INTO products (
    brand_id,
    category_id,
    name,
    slug,
    description,
    base_price,
    active
)
VALUES
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'adidas'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-footwear'
        ),
        'Adidas Runfalcon Running Shoes',
        'adidas-runfalcon-running-shoes',
        'Lightweight everyday running shoes designed for training, walking and active casual wear.',
        69.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'levis'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-jeans'
        ),
        'Levi''s 511 Slim Fit Jeans',
        'levis-511-slim-fit-jeans',
        'Classic slim-fit denim with a streamlined silhouette for everyday casual wear.',
        59.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'puma'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-tshirts'
        ),
        'Puma Essential Logo T-Shirt',
        'puma-essential-logo-tshirt',
        'Soft cotton crew-neck T-shirt designed for casual and active everyday styling.',
        29.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'tommy-hilfiger'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-shirts'
        ),
        'Tommy Hilfiger Oxford Shirt',
        'tommy-hilfiger-oxford-shirt',
        'Classic button-down Oxford shirt suitable for office, date-night and smart-casual outfits.',
        74.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'allen-solly'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-shirts'
        ),
        'Allen Solly Slim Fit Formal Shirt',
        'allen-solly-slim-fit-formal-shirt',
        'Slim-fit formal shirt designed for professional and business-casual wardrobes.',
        44.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'adidas'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-jackets'
        ),
        'Adidas Essentials Track Jacket',
        'adidas-essentials-track-jacket',
        'Versatile track jacket for workouts, travel and casual layering.',
        64.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'levis'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-jackets'
        ),
        'Levi''s Trucker Denim Jacket',
        'levis-trucker-denim-jacket',
        'Classic denim trucker jacket for casual layering and cool-weather outfits.',
        89.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'puma'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-footwear'
        ),
        'Puma Smash Casual Sneakers',
        'puma-smash-casual-sneakers',
        'Clean low-profile sneakers for casual outfits, travel and everyday wear.',
        54.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'tommy-hilfiger'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-tshirts'
        ),
        'Tommy Hilfiger Classic Polo',
        'tommy-hilfiger-classic-polo',
        'Classic polo shirt for smart-casual, vacation and date-night styling.',
        49.99,
        TRUE
    ),
    (
        (
            SELECT id
            FROM brands
            WHERE slug = 'nike'
        ),
        (
            SELECT id
            FROM categories
            WHERE slug = 'mens-footwear'
        ),
        'Nike Revolution Running Shoes',
        'nike-revolution-running-shoes',
        'Cushioned running shoes for daily training, walking and active lifestyles.',
        74.99,
        TRUE
    )
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- PRODUCT VARIANTS
-- ============================================================

-- Adidas Runfalcon

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'adidas-runfalcon-running-shoes'),
        'AD-RUN-BLK-9',
        '9',
        'Black',
        'Running',
        'Mesh / Synthetic',
        69.99,
        8,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-runfalcon-running-shoes'),
        'AD-RUN-BLK-10',
        '10',
        'Black',
        'Running',
        'Mesh / Synthetic',
        69.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-runfalcon-running-shoes'),
        'AD-RUN-BLK-11',
        '11',
        'Black',
        'Running',
        'Mesh / Synthetic',
        69.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Levi's 511

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'levis-511-slim-fit-jeans'),
        'LV-511-BLU-32',
        '32',
        'Dark Blue',
        'Slim Fit',
        'Denim',
        59.99,
        7,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'levis-511-slim-fit-jeans'),
        'LV-511-BLU-34',
        '34',
        'Dark Blue',
        'Slim Fit',
        'Denim',
        59.99,
        4,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'levis-511-slim-fit-jeans'),
        'LV-511-BLU-36',
        '36',
        'Dark Blue',
        'Slim Fit',
        'Denim',
        59.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Puma T-Shirt

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'puma-essential-logo-tshirt'),
        'PM-ESS-BLK-M',
        'M',
        'Black',
        'Regular Fit',
        'Cotton',
        29.99,
        10,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-essential-logo-tshirt'),
        'PM-ESS-BLK-L',
        'L',
        'Black',
        'Regular Fit',
        'Cotton',
        29.99,
        6,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-essential-logo-tshirt'),
        'PM-ESS-BLK-XL',
        'XL',
        'Black',
        'Regular Fit',
        'Cotton',
        29.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Tommy Oxford Shirt

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-oxford-shirt'),
        'TH-OXF-BLU-M',
        'M',
        'Light Blue',
        'Regular Fit',
        'Cotton',
        74.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-oxford-shirt'),
        'TH-OXF-BLU-L',
        'L',
        'Light Blue',
        'Regular Fit',
        'Cotton',
        74.99,
        4,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-oxford-shirt'),
        'TH-OXF-BLU-XL',
        'XL',
        'Light Blue',
        'Regular Fit',
        'Cotton',
        74.99,
        2,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Allen Solly Formal Shirt

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'allen-solly-slim-fit-formal-shirt'),
        'AS-FRM-WHT-M',
        'M',
        'White',
        'Slim Fit',
        'Cotton Blend',
        44.99,
        8,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'allen-solly-slim-fit-formal-shirt'),
        'AS-FRM-WHT-L',
        'L',
        'White',
        'Slim Fit',
        'Cotton Blend',
        44.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'allen-solly-slim-fit-formal-shirt'),
        'AS-FRM-WHT-XL',
        'XL',
        'White',
        'Slim Fit',
        'Cotton Blend',
        44.99,
        1,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Adidas Track Jacket

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'adidas-essentials-track-jacket'),
        'AD-TRK-NVY-M',
        'M',
        'Navy',
        'Track',
        'Polyester',
        64.99,
        6,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-essentials-track-jacket'),
        'AD-TRK-NVY-L',
        'L',
        'Navy',
        'Track',
        'Polyester',
        64.99,
        4,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-essentials-track-jacket'),
        'AD-TRK-NVY-XL',
        'XL',
        'Navy',
        'Track',
        'Polyester',
        64.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Levi's Trucker Jacket

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'levis-trucker-denim-jacket'),
        'LV-TRK-BLU-M',
        'M',
        'Blue',
        'Trucker',
        'Denim',
        89.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'levis-trucker-denim-jacket'),
        'LV-TRK-BLU-L',
        'L',
        'Blue',
        'Trucker',
        'Denim',
        89.99,
        3,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'levis-trucker-denim-jacket'),
        'LV-TRK-BLU-XL',
        'XL',
        'Blue',
        'Trucker',
        'Denim',
        89.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Puma Sneakers

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'puma-smash-casual-sneakers'),
        'PM-SMS-WHT-9',
        '9',
        'White',
        'Casual',
        'Synthetic',
        54.99,
        6,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-smash-casual-sneakers'),
        'PM-SMS-WHT-10',
        '10',
        'White',
        'Casual',
        'Synthetic',
        54.99,
        4,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-smash-casual-sneakers'),
        'PM-SMS-WHT-11',
        '11',
        'White',
        'Casual',
        'Synthetic',
        54.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Tommy Polo

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-classic-polo'),
        'TH-POL-NVY-M',
        'M',
        'Navy',
        'Classic Fit',
        'Cotton',
        49.99,
        7,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-classic-polo'),
        'TH-POL-NVY-L',
        'L',
        'Navy',
        'Classic Fit',
        'Cotton',
        49.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-classic-polo'),
        'TH-POL-NVY-XL',
        'XL',
        'Navy',
        'Classic Fit',
        'Cotton',
        49.99,
        2,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- Nike Revolution

INSERT INTO product_variants (
    product_id,
    sku,
    size,
    color,
    style,
    material,
    price,
    stock_quantity,
    active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'nike-revolution-running-shoes'),
        'NK-REV-BLK-9',
        '9',
        'Black',
        'Running',
        'Mesh / Synthetic',
        74.99,
        8,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'nike-revolution-running-shoes'),
        'NK-REV-BLK-10',
        '10',
        'Black',
        'Running',
        'Mesh / Synthetic',
        74.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'nike-revolution-running-shoes'),
        'NK-REV-BLK-11',
        '11',
        'Black',
        'Running',
        'Mesh / Synthetic',
        74.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- ============================================================
-- PRIMARY PRODUCT IMAGES
-- ============================================================
-- Placeholder image URLs are intentionally used in the initial
-- portfolio implementation. Object storage can replace these later.

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Adidas+Runfalcon',
    'Adidas Runfalcon Running Shoes',
    0,
    TRUE
FROM products p
WHERE p.slug = 'adidas-runfalcon-running-shoes'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Levis+511',
    'Levi''s 511 Slim Fit Jeans',
    0,
    TRUE
FROM products p
WHERE p.slug = 'levis-511-slim-fit-jeans'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Puma+T-Shirt',
    'Puma Essential Logo T-Shirt',
    0,
    TRUE
FROM products p
WHERE p.slug = 'puma-essential-logo-tshirt'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Tommy+Oxford',
    'Tommy Hilfiger Oxford Shirt',
    0,
    TRUE
FROM products p
WHERE p.slug = 'tommy-hilfiger-oxford-shirt'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Allen+Solly',
    'Allen Solly Slim Fit Formal Shirt',
    0,
    TRUE
FROM products p
WHERE p.slug = 'allen-solly-slim-fit-formal-shirt'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Adidas+Track+Jacket',
    'Adidas Essentials Track Jacket',
    0,
    TRUE
FROM products p
WHERE p.slug = 'adidas-essentials-track-jacket'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Levis+Trucker',
    'Levi''s Trucker Denim Jacket',
    0,
    TRUE
FROM products p
WHERE p.slug = 'levis-trucker-denim-jacket'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Puma+Sneakers',
    'Puma Smash Casual Sneakers',
    0,
    TRUE
FROM products p
WHERE p.slug = 'puma-smash-casual-sneakers'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Tommy+Polo',
    'Tommy Hilfiger Classic Polo',
    0,
    TRUE
FROM products p
WHERE p.slug = 'tommy-hilfiger-classic-polo'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Nike+Revolution',
    'Nike Revolution Running Shoes',
    0,
    TRUE
FROM products p
WHERE p.slug = 'nike-revolution-running-shoes'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );


-- ============================================================
-- OCCASION MAPPINGS
-- ============================================================

INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'sports-activewear',
        'casual'
    )
WHERE p.slug =
      'adidas-runfalcon-running-shoes'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'casual',
        'date-night'
    )
WHERE p.slug =
      'levis-511-slim-fit-jeans'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'casual',
        'summer',
        'sports-activewear'
    )
WHERE p.slug =
      'puma-essential-logo-tshirt'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'office',
        'date-night',
        'party'
    )
WHERE p.slug =
      'tommy-hilfiger-oxford-shirt'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'office',
        'wedding'
    )
WHERE p.slug =
      'allen-solly-slim-fit-formal-shirt'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'sports-activewear',
        'casual',
        'winter'
    )
WHERE p.slug =
      'adidas-essentials-track-jacket'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'casual',
        'date-night',
        'winter'
    )
WHERE p.slug =
      'levis-trucker-denim-jacket'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'casual',
        'vacation'
    )
WHERE p.slug =
      'puma-smash-casual-sneakers'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'casual',
        'date-night',
        'vacation',
        'summer'
    )
WHERE p.slug =
      'tommy-hilfiger-classic-polo'
ON CONFLICT DO NOTHING;


INSERT INTO product_occasions (
    product_id,
    occasion_id
)
SELECT
    p.id,
    o.id
FROM products p
JOIN occasions o
    ON o.slug IN (
        'sports-activewear',
        'casual'
    )
WHERE p.slug =
      'nike-revolution-running-shoes'
ON CONFLICT DO NOTHING;