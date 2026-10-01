package com.fashionsense.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Fashion Sense API",
                version = "1.0",
                description = """
                        REST API for the Fashion Sense ecommerce platform.

                        Features include:
                        - Product catalog and discovery
                        - Brands, categories and occasions
                        - Product variants and inventory availability
                        - Customer authentication
                        - Saved addresses
                        - Wishlist
                        - Shopping cart
                        - Checkout
                        - Orders
                        - JWT-based authorization
                        """
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter the JWT access token returned by /api/auth/login"
)
public class OpenApiConfig {
}