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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        errorBody(
                                404,
                                "Not Found",
                                ex.getMessage(),
                                request.getRequestURI()
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
            EmailAlreadyVerifiedException.class,
            CartItemUnavailableException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        errorBody(
                                409,
                                "Conflict",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        errorBody(
                                401,
                                "Unauthorized",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(
            AccountNotActiveException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        errorBody(
                                403,
                                "Forbidden",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler({
            InvalidVerificationTokenException.class,
            EmptyCartException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        errorBody(
                                400,
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        Map<String, Object> body = errorBody(
                400,
                "Bad Request",
                "Request validation failed",
                request.getRequestURI()
        );

        body.put("validationErrors", validationErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        errorBody(
                                400,
                                "Bad Request",
                                "Request constraint validation failed",
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        errorBody(
                                400,
                                "Bad Request",
                                "Malformed or unreadable request body",
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedError(
            Exception ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        errorBody(
                                500,
                                "Internal Server Error",
                                "An unexpected error occurred",
                                request.getRequestURI()
                        )
                );
    }

    private Map<String, Object> errorBody(
            int status,
            String error,
            String message,
            String path
    ) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);

        return body;
    }
}