package com.fashionsense.catalog.image.storage;

import com.fashionsense.catalog.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ImageUploadServiceTest {
    private final S3Client client = mock(S3Client.class);
    private final ProductRepository products = mock(ProductRepository.class);
    private S3ImageUploadService service;

    @BeforeEach
    void setup() {
        service = new S3ImageUploadService(client, products, "test-bucket", "https://images.example.test");
        when(products.existsById(1L)).thenReturn(true);
    }

    @Test
    void uploadsUnderProductPrefixWithGeneratedName() throws Exception {
        byte[] png = java.util.Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aG6kAAAAASUVORK5CYII=");
        var uploaded = service.upload(1L, new MockMultipartFile("file", "../../bad.png", "image/png", png));
        assertTrue(uploaded.objectKey().startsWith("products/1/"));
        assertFalse(uploaded.objectKey().contains(".."));
        assertEquals("https://images.example.test/" + uploaded.objectKey(), uploaded.imageUrl());
        assertEquals(png.length, uploaded.sizeBytes());
        verify(client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void rejectsUnsupportedOrDisguisedFilesWithoutUpload() {
        assertThrows(IllegalArgumentException.class, () -> service.upload(1L,
                new MockMultipartFile("file", "x.svg", "image/svg+xml", "<svg/>".getBytes())));
        assertThrows(IllegalArgumentException.class, () -> service.upload(1L,
                new MockMultipartFile("file", "x.png", "image/png", "<script>bad</script>".getBytes())));
        verifyNoInteractions(client);
    }

    @Test
    void rejectsEmptyAndOversizedFilesWithoutUpload() {
        assertThrows(IllegalArgumentException.class, () -> service.upload(1L,
                new MockMultipartFile("file", "x.png", "image/png", new byte[0])));
        assertThrows(IllegalArgumentException.class, () -> service.upload(1L,
                new MockMultipartFile("file", "x.png", "image/png", new byte[5 * 1024 * 1024 + 1])));
        verifyNoInteractions(client);
    }

    @Test
    void rejectsMissingProductAndUnsafeConfiguration() {
        assertThrows(com.fashionsense.catalog.product.ProductNotFoundException.class, () -> service.upload(2L,
                new MockMultipartFile("file", "x.png", "image/png", new byte[8])));
        assertThrows(IllegalArgumentException.class, () -> new S3ImageUploadService(client, products,
                "test-bucket", "http://images.example.test"));
        verifyNoInteractions(client);
    }
}
