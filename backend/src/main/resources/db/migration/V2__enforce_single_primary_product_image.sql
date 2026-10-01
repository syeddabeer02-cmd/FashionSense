CREATE UNIQUE INDEX ux_product_images_one_primary
    ON product_images(product_id)
    WHERE primary_image = TRUE;