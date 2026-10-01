package com.fashionsense.auth.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerificationDispatchService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    VerificationDispatchService.class
            );

    public void sendVerificationToken(
            String email,
            String rawToken
    ) {

        log.info(
                "EMAIL VERIFICATION TOKEN for {}: {}",
                email,
                rawToken
        );
    }
}