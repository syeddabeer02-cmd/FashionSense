package com.fashionsense.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret}")
            String encodedSecret
    ) {

        byte[] keyBytes =
                Base64.getDecoder()
                        .decode(encodedSecret);

        return new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey secretKey
    ) {

        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey
    ) {

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName(
                "role"
        );

        authoritiesConverter.setAuthorityPrefix(
                "ROLE_"
        );

        JwtAuthenticationConverter jwtConverter =
                new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return jwtConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Public infrastructure health checks.
                         *
                         * These endpoints can be called by:
                         * - Docker health checks
                         * - load balancers
                         * - Kubernetes probes
                         * - deployment platforms
                         *
                         * Detailed health information remains hidden
                         * through application.properties.
                         */
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/liveness",
                                "/actuator/health/readiness"
                        )
                        .permitAll()

                        /*
                         * Swagger / OpenAPI documentation.
                         *
                         * These routes are public so developers
                         * can open the API documentation without
                         * already having a JWT.
                         *
                         * Protected API operations themselves
                         * remain protected by the rules below.
                         */
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        /*
                         * Authentication endpoints.
                         *
                         * Registration, login, email verification
                         * and resend-verification must be reachable
                         * before the user has a JWT.
                         */
                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()

                        /*
                         * Public catalog read operations.
                         *
                         * Customers should be able to browse the
                         * catalog without being logged in.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**",
                                "/api/brands/**",
                                "/api/categories/**",
                                "/api/occasions/**",
                                "/api/variants/**",
                                "/api/product-images/**"
                        )
                        .permitAll()

                        /*
                         * Catalog write operations are ADMIN only.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**",
                                "/api/brands/**",
                                "/api/categories/**",
                                "/api/occasions/**",
                                "/api/variants/**",
                                "/api/product-images/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * Image deletion is also ADMIN only.
                         */
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/product-images/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * Customer-specific APIs require a
                         * successfully authenticated JWT.
                         *
                         * This covers:
                         * - customer profile
                         * - addresses
                         * - wishlist
                         * - cart
                         * - checkout
                         * - orders
                         */
                        .requestMatchers(
                                "/api/customers/**"
                        )
                        .authenticated()

                        /*
                         * Secure everything else by default.
                         *
                         * This means /actuator/info also requires
                         * a valid JWT even though it is exposed
                         * over HTTP.
                         */
                        .anyRequest()
                        .authenticated()
                )

                /*
                 * Spring Security validates Bearer JWTs here.
                 */
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}