package com.fashionsense.catalog.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @GetMapping("/{slug}")
    public ProductResponse getProductBySlug(
            @PathVariable String slug
    ) {
        return ProductResponse.from(
                productService.getProductBySlug(slug)
        );
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
}