package com.fashionsense.catalog.image;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(
            ProductImageService productImageService
    ) {
        this.productImageService = productImageService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductImageResponse> getImagesByProduct(
            @PathVariable Long productId
    ) {
        return productImageService
                .getImagesByProductId(productId)
                .stream()
                .map(ProductImageResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse addImage(
            @Valid @RequestBody ProductImageRequest request
    ) {

        ProductImage image = productImageService.addImage(
                request.productId(),
                request.imageUrl(),
                request.altText(),
                request.displayOrder(),
                request.primaryImage()
        );

        return ProductImageResponse.from(image);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @PathVariable Long imageId
    ) {
        productImageService.deleteImage(imageId);
    }
}