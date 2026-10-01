package com.fashionsense.catalog.brand;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<BrandResponse> getAllBrands() {
        return brandService.getAllBrands()
                .stream()
                .map(BrandResponse::from)
                .toList();
    }

    @GetMapping("/{slug}")
    public BrandResponse getBrandBySlug(@PathVariable String slug) {
        Brand brand = brandService.getBrandBySlug(slug);
        return BrandResponse.from(brand);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse createBrand(
            @Valid @RequestBody BrandRequest request
    ) {

        Brand brand = brandService.createBrand(
                request.name(),
                request.slug(),
                request.logoUrl(),
                request.description()
        );

        return BrandResponse.from(brand);
    }
}