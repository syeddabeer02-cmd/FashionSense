package com.fashionsense.auth;

import com.fashionsense.customer.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration-minutes}")
            long expirationMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user) {

        Instant now = Instant.now();

        Instant expiresAt =
                now.plus(
                        Duration.ofMinutes(
                                expirationMinutes
                        )
                );

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("fashionsense")
                        .issuedAt(now)
                        .expiresAt(expiresAt)
                        .subject(user.getEmail())
                        .claim(
                                "userId",
                                user.getId()
                        )
                        .claim(
                                "role",
                                user.getRole().name()
                        )
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                claims
                        )
                )
                .getTokenValue();
    }

    public long getExpirationSeconds() {
        return Duration
                .ofMinutes(expirationMinutes)
                .toSeconds();
    }
}