package com.fashionsense.catalog.image.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/product-images")
@ConditionalOnProperty(name = "app.images.s3.enabled", havingValue = "true")
public class S3ImageUploadController {
    private final S3ImageUploadService uploads;

    public S3ImageUploadController(S3ImageUploadService uploads) { this.uploads = uploads; }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public S3ImageUploadService.UploadedImage upload(@RequestParam Long productId,
            @RequestPart MultipartFile file) throws IOException {
        return uploads.upload(productId, file);
    }
}
