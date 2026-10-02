-- Fashion Sense department catalog expansion.
-- Adds Women, Kids and Accessories departments with representative
-- products, variants, inventory, images and occasion mappings.

-- ============================================================
-- DEPARTMENTS
-- ============================================================

INSERT INTO categories (name, slug, parent_id)
VALUES
    ('Women', 'women', NULL),
    ('Kids', 'kids', NULL),
    ('Accessories', 'accessories', NULL)
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- WOMEN CATEGORIES
-- ============================================================

INSERT INTO categories (name, slug, parent_id)
VALUES
    (
        'Clothing',
        'womens-clothing',
        (SELECT id FROM categories WHERE slug = 'women')
    ),
    (
        'Footwear',
        'womens-footwear',
        (SELECT id FROM categories WHERE slug = 'women')
    )
ON CONFLICT (slug) DO NOTHING;

INSERT INTO categories (name, slug, parent_id)
VALUES
    (
        'Dresses',
        'womens-dresses',
        (SELECT id FROM categories WHERE slug = 'womens-clothing')
    ),
    (
        'Tops',
        'womens-tops',
        (SELECT id FROM categories WHERE slug = 'womens-clothing')
    )
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- KIDS CATEGORIES
-- ============================================================

INSERT INTO categories (name, slug, parent_id)
VALUES
    (
        'Clothing',
        'kids-clothing',
        (SELECT id FROM categories WHERE slug = 'kids')
    ),
    (
        'Footwear',
        'kids-footwear',
        (SELECT id FROM categories WHERE slug = 'kids')
    )
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- ACCESSORY CATEGORIES
-- ============================================================

INSERT INTO categories (name, slug, parent_id)
VALUES
    (
        'Bags',
        'accessories-bags',
        (SELECT id FROM categories WHERE slug = 'accessories')
    ),
    (
        'Caps',
        'accessories-caps',
        (SELECT id FROM categories WHERE slug = 'accessories')
    ),
    (
        'Wallets',
        'accessories-wallets',
        (SELECT id FROM categories WHERE slug = 'accessories')
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
        (SELECT id FROM brands WHERE slug = 'tommy-hilfiger'),
        (SELECT id FROM categories WHERE slug = 'womens-dresses'),
        'Tommy Hilfiger Women Midi Dress',
        'tommy-hilfiger-women-midi-dress',
        'Elegant midi dress designed for parties, date nights and smart casual occasions.',
        89.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'puma'),
        (SELECT id FROM categories WHERE slug = 'womens-tops'),
        'Puma Women Essential Training Top',
        'puma-women-essential-training-top',
        'Lightweight training top for workouts, active lifestyles and casual everyday wear.',
        34.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'adidas'),
        (SELECT id FROM categories WHERE slug = 'womens-footwear'),
        'Adidas Women Cloudfoam Running Shoes',
        'adidas-women-cloudfoam-running-shoes',
        'Cushioned running shoes designed for walking, training and active casual wear.',
        79.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'nike'),
        (SELECT id FROM categories WHERE slug = 'kids-clothing'),
        'Nike Kids Sportswear Hoodie',
        'nike-kids-sportswear-hoodie',
        'Comfortable kids hoodie for school, casual outings and cool-weather layering.',
        44.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'adidas'),
        (SELECT id FROM categories WHERE slug = 'kids-clothing'),
        'Adidas Kids Essentials T-Shirt',
        'adidas-kids-essentials-tshirt',
        'Soft everyday T-shirt for play, sports and casual outfits.',
        24.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'puma'),
        (SELECT id FROM categories WHERE slug = 'kids-footwear'),
        'Puma Kids Courtflex Sneakers',
        'puma-kids-courtflex-sneakers',
        'Everyday kids sneakers designed for comfort, play and casual wear.',
        39.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'adidas'),
        (SELECT id FROM categories WHERE slug = 'accessories-bags'),
        'Adidas Classic Backpack',
        'adidas-classic-backpack',
        'Versatile backpack for commuting, workouts, school and everyday travel.',
        39.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'puma'),
        (SELECT id FROM categories WHERE slug = 'accessories-caps'),
        'Puma Essential Baseball Cap',
        'puma-essential-baseball-cap',
        'Classic adjustable cap for casual outfits, travel and outdoor activities.',
        24.99,
        TRUE
    ),
    (
        (SELECT id FROM brands WHERE slug = 'tommy-hilfiger'),
        (SELECT id FROM categories WHERE slug = 'accessories-wallets'),
        'Tommy Hilfiger Classic Wallet',
        'tommy-hilfiger-classic-wallet',
        'Compact everyday wallet with a clean classic design.',
        39.99,
        TRUE
    )
ON CONFLICT (slug) DO NOTHING;


-- ============================================================
-- WOMEN VARIANTS
-- ============================================================

INSERT INTO product_variants (
    product_id, sku, size, color, style,
    material, price, stock_quantity, active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-women-midi-dress'),
        'TH-WDR-NVY-S',
        'S',
        'Navy',
        'Midi',
        'Viscose Blend',
        89.99,
        6,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-women-midi-dress'),
        'TH-WDR-NVY-M',
        'M',
        'Navy',
        'Midi',
        'Viscose Blend',
        89.99,
        4,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-women-midi-dress'),
        'TH-WDR-NVY-L',
        'L',
        'Navy',
        'Midi',
        'Viscose Blend',
        89.99,
        0,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-women-essential-training-top'),
        'PM-WTP-PNK-S',
        'S',
        'Pink',
        'Training',
        'Polyester',
        34.99,
        8,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-women-essential-training-top'),
        'PM-WTP-PNK-M',
        'M',
        'Pink',
        'Training',
        'Polyester',
        34.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-women-essential-training-top'),
        'PM-WTP-PNK-L',
        'L',
        'Pink',
        'Training',
        'Polyester',
        34.99,
        0,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-women-cloudfoam-running-shoes'),
        'AD-WCF-WHT-7',
        '7',
        'White',
        'Running',
        'Mesh / Synthetic',
        79.99,
        7,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-women-cloudfoam-running-shoes'),
        'AD-WCF-WHT-8',
        '8',
        'White',
        'Running',
        'Mesh / Synthetic',
        79.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-women-cloudfoam-running-shoes'),
        'AD-WCF-WHT-9',
        '9',
        'White',
        'Running',
        'Mesh / Synthetic',
        79.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- ============================================================
-- KIDS VARIANTS
-- ============================================================

INSERT INTO product_variants (
    product_id, sku, size, color, style,
    material, price, stock_quantity, active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'nike-kids-sportswear-hoodie'),
        'NK-KHD-GRY-8',
        '8Y',
        'Grey',
        'Hoodie',
        'Cotton Blend',
        44.99,
        8,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'nike-kids-sportswear-hoodie'),
        'NK-KHD-GRY-10',
        '10Y',
        'Grey',
        'Hoodie',
        'Cotton Blend',
        44.99,
        5,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'nike-kids-sportswear-hoodie'),
        'NK-KHD-GRY-12',
        '12Y',
        'Grey',
        'Hoodie',
        'Cotton Blend',
        44.99,
        0,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-kids-essentials-tshirt'),
        'AD-KTS-BLU-8',
        '8Y',
        'Blue',
        'Regular Fit',
        'Cotton',
        24.99,
        10,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-kids-essentials-tshirt'),
        'AD-KTS-BLU-10',
        '10Y',
        'Blue',
        'Regular Fit',
        'Cotton',
        24.99,
        6,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'adidas-kids-essentials-tshirt'),
        'AD-KTS-BLU-12',
        '12Y',
        'Blue',
        'Regular Fit',
        'Cotton',
        24.99,
        0,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-kids-courtflex-sneakers'),
        'PM-KCF-WHT-3',
        '3',
        'White',
        'Casual',
        'Synthetic',
        39.99,
        7,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-kids-courtflex-sneakers'),
        'PM-KCF-WHT-4',
        '4',
        'White',
        'Casual',
        'Synthetic',
        39.99,
        4,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-kids-courtflex-sneakers'),
        'PM-KCF-WHT-5',
        '5',
        'White',
        'Casual',
        'Synthetic',
        39.99,
        0,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- ============================================================
-- ACCESSORY VARIANTS
-- ============================================================

INSERT INTO product_variants (
    product_id, sku, size, color, style,
    material, price, stock_quantity, active
)
VALUES
    (
        (SELECT id FROM products WHERE slug = 'adidas-classic-backpack'),
        'AD-BAG-BLK-OS',
        'One Size',
        'Black',
        'Backpack',
        'Polyester',
        39.99,
        9,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'puma-essential-baseball-cap'),
        'PM-CAP-BLK-OS',
        'One Size',
        'Black',
        'Baseball Cap',
        'Cotton',
        24.99,
        11,
        TRUE
    ),
    (
        (SELECT id FROM products WHERE slug = 'tommy-hilfiger-classic-wallet'),
        'TH-WAL-BRN-OS',
        'One Size',
        'Brown',
        'Bifold',
        'Synthetic Leather',
        39.99,
        6,
        TRUE
    )
ON CONFLICT (sku) DO NOTHING;


-- ============================================================
-- PRIMARY IMAGES
-- ============================================================

INSERT INTO product_images (
    product_id,
    image_url,
    alt_text,
    display_order,
    primary_image
)
SELECT
    p.id,
    'https://placehold.co/800x1000?text=Women+Midi+Dress',
    'Tommy Hilfiger Women Midi Dress',
    0,
    TRUE
FROM products p
WHERE p.slug = 'tommy-hilfiger-women-midi-dress'
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
    'https://placehold.co/800x1000?text=Women+Training+Top',
    'Puma Women Essential Training Top',
    0,
    TRUE
FROM products p
WHERE p.slug = 'puma-women-essential-training-top'
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
    'https://placehold.co/800x1000?text=Women+Running+Shoes',
    'Adidas Women Cloudfoam Running Shoes',
    0,
    TRUE
FROM products p
WHERE p.slug = 'adidas-women-cloudfoam-running-shoes'
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
    'https://placehold.co/800x1000?text=Kids+Hoodie',
    'Nike Kids Sportswear Hoodie',
    0,
    TRUE
FROM products p
WHERE p.slug = 'nike-kids-sportswear-hoodie'
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
    'https://placehold.co/800x1000?text=Kids+T-Shirt',
    'Adidas Kids Essentials T-Shirt',
    0,
    TRUE
FROM products p
WHERE p.slug = 'adidas-kids-essentials-tshirt'
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
    'https://placehold.co/800x1000?text=Kids+Sneakers',
    'Puma Kids Courtflex Sneakers',
    0,
    TRUE
FROM products p
WHERE p.slug = 'puma-kids-courtflex-sneakers'
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
    'https://placehold.co/800x1000?text=Adidas+Backpack',
    'Adidas Classic Backpack',
    0,
    TRUE
FROM products p
WHERE p.slug = 'adidas-classic-backpack'
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
    'https://placehold.co/800x1000?text=Puma+Cap',
    'Puma Essential Baseball Cap',
    0,
    TRUE
FROM products p
WHERE p.slug = 'puma-essential-baseball-cap'
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
    'https://placehold.co/800x1000?text=Tommy+Wallet',
    'Tommy Hilfiger Classic Wallet',
    0,
    TRUE
FROM products p
WHERE p.slug = 'tommy-hilfiger-classic-wallet'
  AND NOT EXISTS (
      SELECT 1
      FROM product_images pi
      WHERE pi.product_id = p.id
        AND pi.primary_image = TRUE
  );


-- ============================================================
-- OCCASION MAPPINGS
-- ============================================================

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('party', 'date-night', 'wedding')
WHERE p.slug = 'tommy-hilfiger-women-midi-dress'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('sports-activewear', 'casual', 'summer')
WHERE p.slug = 'puma-women-essential-training-top'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('sports-activewear', 'casual')
WHERE p.slug = 'adidas-women-cloudfoam-running-shoes'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('casual', 'winter')
WHERE p.slug = 'nike-kids-sportswear-hoodie'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('casual', 'summer', 'sports-activewear')
WHERE p.slug = 'adidas-kids-essentials-tshirt'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('casual', 'sports-activewear')
WHERE p.slug = 'puma-kids-courtflex-sneakers'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('casual', 'vacation', 'sports-activewear')
WHERE p.slug = 'adidas-classic-backpack'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('casual', 'vacation', 'summer')
WHERE p.slug = 'puma-essential-baseball-cap'
ON CONFLICT DO NOTHING;

INSERT INTO product_occasions (product_id, occasion_id)
SELECT p.id, o.id
FROM products p
JOIN occasions o
    ON o.slug IN ('office', 'date-night')
WHERE p.slug = 'tommy-hilfiger-classic-wallet'
ON CONFLICT DO NOTHING;