package com.fashionsense.common;

import com.fashionsense.catalog.brand.BrandAlreadyExistsException;
import com.fashionsense.catalog.brand.BrandNotFoundException;
import com.fashionsense.catalog.category.CategoryAlreadyExistsException;
import com.fashionsense.catalog.category.CategoryNotFoundException;
import com.fashionsense.catalog.image.ProductImageNotFoundException;
import com.fashionsense.catalog.image.ProductPrimaryImageAlreadyExistsException;
import com.fashionsense.catalog.occasion.OccasionNotFoundException;
import com.fashionsense.catalog.product.ProductAlreadyExistsException;
import com.fashionsense.catalog.product.ProductNotFoundException;
import com.fashionsense.catalog.variant.ProductVariantAlreadyExistsException;
import com.fashionsense.catalog.variant.ProductVariantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            BrandNotFoundException.class,
            CategoryNotFoundException.class,
            ProductNotFoundException.class,
            ProductVariantNotFoundException.class,
            ProductImageNotFoundException.class,
            OccasionNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(
            RuntimeException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(
                        404,
                        "Not Found",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler({
            BrandAlreadyExistsException.class,
            CategoryAlreadyExistsException.class,
            ProductAlreadyExistsException.class,
            ProductVariantAlreadyExistsException.class,
            ProductPrimaryImageAlreadyExistsException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(
            RuntimeException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorBody(
                        409,
                        "Conflict",
                        ex.getMessage()
                ));
    }

    private Map<String, Object> errorBody(
            int status,
            String error,
            String message
    ) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status,
                "error", error,
                "message", message
        );
    }
}