package com.fashionsense.auth.verification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken>
    findByTokenHashAndUsedFalse(
            String tokenHash
    );

    List<EmailVerificationToken>
    findByUserIdAndUsedFalse(
            Long userId
    );
}