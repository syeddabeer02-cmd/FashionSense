package com.fashionsense.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {
    private final SecurityConfig config = new SecurityConfig();
    private final String secret = Base64.getEncoder().encodeToString(
            "test-only-key-at-least-32-bytes-long".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    private String token(String issuer, Instant expiry, String role) {
        var claims = JwtClaimsSet.builder().issuer(issuer).subject("customer@example.test")
                .issuedAt(Instant.now().minusSeconds(1200)).expiresAt(expiry)
                .claim("userId", 1L).claim("role", role).build();
        return config.jwtEncoder(config.jwtSecretKey(secret))
                .encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Test
    void acceptsValidTokenAndMapsCustomerRole() {
        var jwt = config.jwtDecoder(config.jwtSecretKey(secret)).decode(
                token("fashionsense", Instant.now().plusSeconds(300), "CUSTOMER"));
        var auth = config.jwtAuthenticationConverter().convert(jwt);
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
        assertFalse(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void rejectsOtherIssuerEvenWithValidSignature() {
        assertThrows(JwtException.class, () -> config.jwtDecoder(config.jwtSecretKey(secret)).decode(
                token("other-application", Instant.now().plusSeconds(300), "ADMIN")));
    }

    @Test
    void rejectsExpiredToken() {
        assertThrows(JwtException.class, () -> config.jwtDecoder(config.jwtSecretKey(secret)).decode(
                token("fashionsense", Instant.now().minusSeconds(300), "CUSTOMER")));
    }

    @Test
    void rejectsTokenSignedByAnotherKey() {
        String other = Base64.getEncoder().encodeToString("another-test-key-with-at-least-32-bytes".getBytes());
        assertThrows(JwtException.class, () -> config.jwtDecoder(config.jwtSecretKey(other)).decode(
                token("fashionsense", Instant.now().plusSeconds(300), "CUSTOMER")));
    }

    @Test
    void rejectsWeakOrMalformedSecret() {
        assertThrows(IllegalArgumentException.class, () -> config.jwtSecretKey("not base64"));
        assertThrows(IllegalArgumentException.class, () -> config.jwtSecretKey(
                Base64.getEncoder().encodeToString("short".getBytes())));
    }
}
