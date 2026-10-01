package com.fashionsense.catalog.product;

import com.fashionsense.catalog.brand.Brand;
import com.fashionsense.catalog.brand.BrandNotFoundException;
import com.fashionsense.catalog.brand.BrandRepository;
import com.fashionsense.catalog.category.Category;
import com.fashionsense.catalog.category.CategoryNotFoundException;
import com.fashionsense.catalog.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            BrandRepository brandRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Product getProductBySlug(String slug) {
        return productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with slug: " + slug
                        )
                );
    }

    @Transactional
    public Product createProduct(
            Long brandId,
            Long categoryId,
            String name,
            String slug,
            String description,
            BigDecimal basePrice
    ) {

        if (productRepository.existsBySlug(slug)) {
            throw new ProductAlreadyExistsException(
                    "Product already exists with slug: " + slug
            );
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() ->
                        new BrandNotFoundException(
                                "Brand not found with id: " + brandId
                        )
                );

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + categoryId
                        )
                );

        Product product = new Product();

        product.setBrand(brand);
        product.setCategory(category);
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setBasePrice(basePrice);
        product.setActive(true);

        return productRepository.save(product);
    }
}