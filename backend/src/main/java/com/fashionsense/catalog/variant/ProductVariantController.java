package com.fashionsense.catalog.variant;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(
            ProductVariantService productVariantService
    ) {
        this.productVariantService = productVariantService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductVariantResponse> getVariantsByProduct(
            @PathVariable Long productId
    ) {
        return productVariantService
                .getVariantsByProductId(productId)
                .stream()
                .map(ProductVariantResponse::from)
                .toList();
    }

    @GetMapping("/sku/{sku}")
    public ProductVariantResponse getVariantBySku(
            @PathVariable String sku
    ) {
        return ProductVariantResponse.from(
                productVariantService.getVariantBySku(sku)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductVariantResponse createVariant(
            @Valid @RequestBody ProductVariantRequest request
    ) {

        ProductVariant variant =
                productVariantService.createVariant(
                        request.productId(),
                        request.sku(),
                        request.size(),
                        request.color(),
                        request.style(),
                        request.material(),
                        request.price(),
                        request.stockQuantity()
                );

        return ProductVariantResponse.from(variant);
    }

    @GetMapping("/admin/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminProductVariantResponse> getAdminVariantsByProduct(
            @PathVariable Long productId
    ) {

        return productVariantService
                .getVariantsByProductId(productId)
                .stream()
                .map(AdminProductVariantResponse::from)
                .toList();
    }

    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminProductVariantResponse updateVariant(
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantUpdateRequest request
    ) {

        ProductVariant variant =
                productVariantService.updateVariant(
                        variantId,
                        request.size(),
                        request.color(),
                        request.style(),
                        request.material(),
                        request.price(),
                        request.stockQuantity(),
                        request.active()
                );

        return AdminProductVariantResponse.from(variant);
    }

    @PutMapping("/{variantId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminProductVariantResponse updateStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequest request
    ) {

        ProductVariant variant =
                productVariantService.updateStock(
                        variantId,
                        request.stockQuantity()
                );

        return AdminProductVariantResponse.from(variant);
    }
}