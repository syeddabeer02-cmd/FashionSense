package com.fashionsense.catalog.brand;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Transactional(readOnly = true)
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Brand getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new BrandNotFoundException(
                                "Brand not found with slug: " + slug
                        )
                );
    }

    @Transactional
    public Brand createBrand(
            String name,
            String slug,
            String logoUrl,
            String description
    ) {

        if (brandRepository.existsByNameIgnoreCase(name)) {
            throw new BrandAlreadyExistsException(
                    "Brand already exists with name: " + name
            );
        }

        if (brandRepository.existsBySlug(slug)) {
            throw new BrandAlreadyExistsException(
                    "Brand already exists with slug: " + slug
            );
        }

        Brand brand = new Brand();

        brand.setName(name);
        brand.setSlug(slug);
        brand.setLogoUrl(logoUrl);
        brand.setDescription(description);
        brand.setActive(true);

        return brandRepository.save(brand);
    }
}