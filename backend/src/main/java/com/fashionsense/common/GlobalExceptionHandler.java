package com.fashionsense.common;

import com.fashionsense.auth.AccountNotActiveException;
import com.fashionsense.auth.EmailAlreadyRegisteredException;
import com.fashionsense.auth.EmailAlreadyVerifiedException;
import com.fashionsense.auth.InvalidCredentialsException;
import com.fashionsense.auth.InvalidVerificationTokenException;
import com.fashionsense.cart.CartItemNotFoundException;
import com.fashionsense.cart.CartItemUnavailableException;
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
import com.fashionsense.customer.address.AddressNotFoundException;
import com.fashionsense.order.EmptyCartException;
import com.fashionsense.order.OrderNotFoundException;
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
            OccasionNotFoundException.class,
            AddressNotFoundException.class,
            CartItemNotFoundException.class,
            OrderNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(
            RuntimeException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        errorBody(
                                404,
                                "Not Found",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler({
            BrandAlreadyExistsException.class,
            CategoryAlreadyExistsException.class,
            ProductAlreadyExistsException.class,
            ProductVariantAlreadyExistsException.class,
            ProductPrimaryImageAlreadyExistsException.class,
            EmailAlreadyRegisteredException.class,
            EmailAlreadyVerifiedException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(
            RuntimeException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        errorBody(
                                409,
                                "Conflict",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            InvalidCredentialsException.class
    )
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            InvalidCredentialsException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        errorBody(
                                401,
                                "Unauthorized",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            AccountNotActiveException.class
    )
    public ResponseEntity<Map<String, Object>> handleForbidden(
            AccountNotActiveException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        errorBody(
                                403,
                                "Forbidden",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler({
            InvalidVerificationTokenException.class,
            CartItemUnavailableException.class,
            EmptyCartException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            RuntimeException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        errorBody(
                                400,
                                "Bad Request",
                                ex.getMessage()
                        )
                );
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