package com.fashionsense.catalog.product;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "id",
                    "name",
                    "basePrice",
                    "createdAt",
                    "updatedAt"
            );

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductResponse> searchProducts(

            @RequestParam(required = false)
            String brand,

            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @RequestParam(required = false)
            String size,

            @RequestParam(required = false)
            String color,

            @RequestParam(required = false)
            String style,

            @RequestParam(required = false)
            String material,

            @RequestParam(required = false)
            String occasion,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int pageSize,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction
    ) {

        int safePage = Math.max(page, 0);

        int safePageSize =
                Math.min(
                        Math.max(pageSize, 1),
                        100
                );

        String safeSortBy =
                ALLOWED_SORT_FIELDS.contains(sortBy)
                        ? sortBy
                        : "id";

        Sort.Direction sortDirection =
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        ProductSearchCriteria criteria =
                new ProductSearchCriteria(
                        brand,
                        category,
                        minPrice,
                        maxPrice,
                        size,
                        color,
                        style,
                        material,
                        occasion
                );

        PageRequest pageable =
                PageRequest.of(
                        safePage,
                        safePageSize,
                        Sort.by(
                                sortDirection,
                                safeSortBy
                        )
                );

        return productService.searchProducts(
                criteria,
                pageable
        );
    }

    @GetMapping("/{slug}")
    public ProductResponse getProductBySlug(
            @PathVariable String slug
    ) {
        return ProductResponse.from(
                productService.getProductBySlug(slug)
        );
    }

    @GetMapping("/{slug}/details")
    public ProductDetailResponse getProductDetail(
            @PathVariable String slug
    ) {
        return productService.getProductDetail(slug);
    }

    @GetMapping("/filter/occasion/{occasionSlug}")
    public List<ProductResponse> getProductsByOccasion(
            @PathVariable String occasionSlug
    ) {
        return productService
                .getProductsByOccasion(occasionSlug)
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        Product product = productService.createProduct(
                request.brandId(),
                request.categoryId(),
                request.name(),
                request.slug(),
                request.description(),
                request.basePrice()
        );

        return ProductResponse.from(product);
    }

    @PostMapping("/{productId}/occasions/{occasionSlug}")
    public ProductResponse addOccasionToProduct(
            @PathVariable Long productId,
            @PathVariable String occasionSlug
    ) {

        return ProductResponse.from(
                productService.addOccasionToProduct(
                        productId,
                        occasionSlug
                )
        );
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {

        Product product =
                productService.updateProduct(
                        productId,
                        request.brandId(),
                        request.categoryId(),
                        request.name(),
                        request.description(),
                        request.basePrice(),
                        request.active()
                );

        return ProductResponse.from(product);
    }
}