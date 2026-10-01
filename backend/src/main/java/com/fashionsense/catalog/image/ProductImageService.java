package com.fashionsense.catalog.image;

import com.fashionsense.catalog.product.Product;
import com.fashionsense.catalog.product.ProductNotFoundException;
import com.fashionsense.catalog.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductRepository productRepository
    ) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductImage> getImagesByProductId(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        return productImageRepository.findByProductIdWithProduct(productId);
    }

    @Transactional
    public ProductImage addImage(
            Long productId,
            String imageUrl,
            String altText,
            int displayOrder,
            boolean primaryImage
    ) {

        if (displayOrder < 0) {
            throw new IllegalArgumentException(
                    "Display order cannot be negative"
            );
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        if (primaryImage &&
                productImageRepository.existsByProductIdAndPrimaryImageTrue(productId)) {

            throw new ProductPrimaryImageAlreadyExistsException(
                    "Product already has a primary image"
            );
        }

        ProductImage image = new ProductImage();

        image.setProduct(product);
        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        image.setDisplayOrder(displayOrder);
        image.setPrimaryImage(primaryImage);

        return productImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long imageId) {

        if (!productImageRepository.existsById(imageId)) {
            throw new ProductImageNotFoundException(
                    "Product image not found with id: " + imageId
            );
        }

        productImageRepository.deleteById(imageId);
    }
}