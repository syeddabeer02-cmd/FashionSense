package com.fashionsense.catalog.image.storage;

import com.fashionsense.catalog.product.ProductNotFoundException;
import com.fashionsense.catalog.product.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.images.s3.enabled", havingValue = "true")
public class S3ImageUploadService {
    private static final long MAX_BYTES = 5 * 1024 * 1024;
    private static final Map<String, String> TYPES = Map.of(
            "image/png", ".png", "image/jpeg", ".jpg", "image/webp", ".webp");
    private final S3Client client;
    private final ProductRepository products;
    private final String bucket;
    private final String publicBase;

    public S3ImageUploadService(S3Client client, ProductRepository products,
            @Value("${app.images.s3.bucket}") String bucket,
            @Value("${app.images.s3.public-base-url}") String publicBase) {
        if (bucket.isBlank()) throw new IllegalArgumentException("S3 image bucket is required");
        URI base = URI.create(publicBase);
        if (!"https".equals(base.getScheme()) || base.getHost() == null || base.getUserInfo() != null
                || base.getQuery() != null || base.getFragment() != null) {
            throw new IllegalArgumentException("S3 image public base must be an HTTPS URL");
        }
        this.client = client;
        this.products = products;
        this.bucket = bucket;
        this.publicBase = publicBase.replaceAll("/+$", "");
    }

    public UploadedImage upload(Long productId, MultipartFile file) throws IOException {
        if (productId == null || productId <= 0) throw new IllegalArgumentException("Invalid product ID");
        if (!products.existsById(productId)) throw new ProductNotFoundException("Product not found");
        String type = file.getContentType();
        if (type == null || !TYPES.containsKey(type)) throw new IllegalArgumentException("Use PNG, JPEG or WebP");
        if (file.isEmpty() || file.getSize() > MAX_BYTES) throw new IllegalArgumentException("Image must be 1 byte to 5 MiB");
        byte[] bytes = file.getBytes();
        if (bytes.length == 0 || bytes.length > MAX_BYTES || !matchesSignature(type, bytes)) {
            throw new IllegalArgumentException("Invalid image content or size");
        }
        String key = "products/" + productId + "/" + UUID.randomUUID() + TYPES.get(type);
        String url = publicBase + "/" + key;
        if (url.length() > 500) throw new IllegalArgumentException("Image URL exceeds database limit");
        client.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
                .contentType(type).contentLength((long) bytes.length).build(), RequestBody.fromBytes(bytes));
        return new UploadedImage(key, url, type, bytes.length);
    }

    private static boolean matchesSignature(String type, byte[] b) {
        return switch (type) {
            case "image/png" -> b.length >= 8 && b[0] == (byte) 0x89 && b[1] == 'P' && b[2] == 'N'
                    && b[3] == 'G' && b[4] == 13 && b[5] == 10 && b[6] == 26 && b[7] == 10;
            case "image/jpeg" -> b.length >= 3 && b[0] == (byte) 0xff && b[1] == (byte) 0xd8 && b[2] == (byte) 0xff;
            case "image/webp" -> b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default -> false;
        };
    }

    public record UploadedImage(String objectKey, String imageUrl, String contentType, long sizeBytes) { }
}
