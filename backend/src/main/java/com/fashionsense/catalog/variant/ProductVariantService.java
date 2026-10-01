package com.fashionsense.catalog.variant;

import com.fashionsense.catalog.product.Product;
import com.fashionsense.catalog.product.ProductNotFoundException;
import com.fashionsense.catalog.product.ProductRepository;
import com.fashionsense.config.ProductCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductCacheService productCacheService;

    public ProductVariantService(
            ProductVariantRepository productVariantRepository,
            ProductRepository productRepository,
            ProductCacheService productCacheService
    ) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
        this.productCacheService = productCacheService;
    }

    @Transactional(readOnly = true)
    public List<ProductVariant> getVariantsByProductId(
            Long productId
    ) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        return productVariantRepository
                .findByProductIdWithProduct(productId);
    }

    @Transactional(readOnly = true)
    public ProductVariant getVariantBySku(
            String sku
    ) {

        return productVariantRepository.findBySku(sku)
                .orElseThrow(() ->
                        new ProductVariantNotFoundException(
                                "Product variant not found with SKU: " + sku
                        )
                );
    }

    @Transactional
    public ProductVariant createVariant(
            Long productId,
            String sku,
            String size,
            String color,
            String style,
            String material,
            BigDecimal price,
            int stockQuantity
    ) {

        if (productVariantRepository.existsBySku(sku)) {
            throw new ProductVariantAlreadyExistsException(
                    "Product variant already exists with SKU: " + sku
            );
        }

        if (stockQuantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative"
            );
        }

        if (price != null
                && price.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Variant price cannot be negative"
            );
        }

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );

        ProductVariant variant =
                new ProductVariant();

        variant.setProduct(product);
        variant.setSku(sku);
        variant.setSize(size);
        variant.setColor(color);
        variant.setStyle(style);
        variant.setMaterial(material);
        variant.setPrice(price);
        variant.setStockQuantity(stockQuantity);
        variant.setActive(true);

        ProductVariant savedVariant =
                productVariantRepository.save(variant);

        productCacheService
                .evictProductDetailAfterCommit(
                        product.getSlug()
                );

        return savedVariant;
    }
}