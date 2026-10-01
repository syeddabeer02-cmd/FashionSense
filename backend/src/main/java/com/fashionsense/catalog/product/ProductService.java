package com.fashionsense.catalog.product;

import com.fashionsense.catalog.brand.Brand;
import com.fashionsense.catalog.brand.BrandNotFoundException;
import com.fashionsense.catalog.brand.BrandRepository;
import com.fashionsense.catalog.category.Category;
import com.fashionsense.catalog.category.CategoryNotFoundException;
import com.fashionsense.catalog.category.CategoryRepository;
import com.fashionsense.catalog.image.ProductImageRepository;
import com.fashionsense.catalog.image.ProductImageResponse;
import com.fashionsense.catalog.occasion.Occasion;
import com.fashionsense.catalog.occasion.OccasionNotFoundException;
import com.fashionsense.catalog.occasion.OccasionRepository;
import com.fashionsense.catalog.variant.ProductVariantRepository;
import com.fashionsense.catalog.variant.ProductVariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final OccasionRepository occasionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(
            ProductRepository productRepository,
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            OccasionRepository occasionRepository,
            ProductVariantRepository productVariantRepository,
            ProductImageRepository productImageRepository
    ) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.occasionRepository = occasionRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            ProductSearchCriteria criteria,
            Pageable pageable
    ) {
        return productRepository
                .findAll(
                        ProductSpecifications.matches(criteria),
                        pageable
                )
                .map(ProductResponse::from);
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

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(String slug) {

        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with slug: " + slug
                        )
                );

        List<ProductVariantResponse> variants =
                productVariantRepository
                        .findByProductIdWithProduct(product.getId())
                        .stream()
                        .map(ProductVariantResponse::from)
                        .toList();

        List<ProductImageResponse> images =
                productImageRepository
                        .findByProductIdWithProduct(product.getId())
                        .stream()
                        .map(ProductImageResponse::from)
                        .toList();

        return new ProductDetailResponse(
                ProductResponse.from(product),
                variants,
                images
        );
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByOccasion(String occasionSlug) {

        if (occasionRepository.findBySlug(occasionSlug).isEmpty()) {
            throw new OccasionNotFoundException(
                    "Occasion not found with slug: " + occasionSlug
            );
        }

        return productRepository.findByOccasionSlug(occasionSlug);
    }

    @Transactional
    public Product addOccasionToProduct(
            Long productId,
            String occasionSlug
    ) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        Occasion occasion = occasionRepository.findBySlug(occasionSlug)
                .orElseThrow(() ->
                        new OccasionNotFoundException(
                                "Occasion not found with slug: " + occasionSlug
                        )
                );

        product.addOccasion(occasion);
        productRepository.save(product);

        return productRepository.findBySlugWithDetails(product.getSlug())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with slug: "
                                        + product.getSlug()
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