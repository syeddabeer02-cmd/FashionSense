package com.fashionsense.common;

import com.fashionsense.catalog.brand.BrandAlreadyExistsException;
import com.fashionsense.catalog.brand.BrandNotFoundException;
import com.fashionsense.catalog.category.CategoryAlreadyExistsException;
import com.fashionsense.catalog.category.CategoryNotFoundException;
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

    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBrandNotFound(
            BrandNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(
                        404,
                        "Not Found",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(BrandAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBrandAlreadyExists(
            BrandAlreadyExistsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorBody(
                        409,
                        "Conflict",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(
            CategoryNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(
                        404,
                        "Not Found",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryAlreadyExists(
            CategoryAlreadyExistsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorBody(
                        409,
                        "Conflict",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(
            ProductNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(
                        404,
                        "Not Found",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleProductAlreadyExists(
            ProductAlreadyExistsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorBody(
                        409,
                        "Conflict",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ProductVariantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductVariantNotFound(
            ProductVariantNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(
                        404,
                        "Not Found",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ProductVariantAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleProductVariantAlreadyExists(
            ProductVariantAlreadyExistsException ex
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