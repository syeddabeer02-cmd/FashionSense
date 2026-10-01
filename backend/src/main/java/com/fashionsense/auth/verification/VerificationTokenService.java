package com.fashionsense.auth.verification;

import com.fashionsense.auth.InvalidVerificationTokenException;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import com.fashionsense.customer.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class VerificationTokenService {

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final long expirationMinutes;

    public VerificationTokenService(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            @Value("${app.email-verification.expiration-minutes}")
            long expirationMinutes
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public String createToken(User user) {

        List<EmailVerificationToken> existingTokens =
                tokenRepository
                        .findByUserIdAndUsedFalse(
                                user.getId()
                        );

        for (EmailVerificationToken token : existingTokens) {
            token.setUsed(true);
        }

        tokenRepository.saveAll(existingTokens);

        String rawToken = generateRawToken();

        EmailVerificationToken token =
                new EmailVerificationToken();

        token.setUser(user);

        token.setTokenHash(
                hashToken(rawToken)
        );

        token.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(expirationMinutes)
        );

        token.setUsed(false);

        tokenRepository.save(token);

        return rawToken;
    }

    @Transactional
    public User verifyToken(
            String rawToken
    ) {

        String tokenHash =
                hashToken(rawToken);

        EmailVerificationToken token =
                tokenRepository
                        .findByTokenHashAndUsedFalse(
                                tokenHash
                        )
                        .orElseThrow(() ->
                                new InvalidVerificationTokenException(
                                        "Verification token is invalid or has already been used"
                                )
                        );

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new InvalidVerificationTokenException(
                    "Verification token has expired"
            );
        }

        User user = token.getUser();

        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);

        token.setUsed(true);

        tokenRepository.save(token);

        return userRepository.save(user);
    }

    private String generateRawToken() {

        byte[] bytes = new byte[32];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(
            String rawToken
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {

            throw new IllegalStateException(
                    "SHA-256 is not available",
                    ex
            );
        }
    }
}