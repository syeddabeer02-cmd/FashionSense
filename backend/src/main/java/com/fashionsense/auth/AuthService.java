package com.fashionsense.auth;

import com.fashionsense.auth.verification.VerificationDispatchService;
import com.fashionsense.auth.verification.VerificationTokenService;
import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import com.fashionsense.customer.UserRole;
import com.fashionsense.customer.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationTokenService verificationTokenService;
    private final VerificationDispatchService verificationDispatchService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            VerificationTokenService verificationTokenService,
            VerificationDispatchService verificationDispatchService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.verificationTokenService = verificationTokenService;
        this.verificationDispatchService = verificationDispatchService;
    }

    @Transactional
    public User register(
            RegisterRequest request
    ) {

        String normalizedEmail =
                request.email()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(
                normalizedEmail
        )) {

            throw new EmailAlreadyRegisteredException(
                    "Email is already registered: "
                            + normalizedEmail
            );
        }

        User user = new User();

        user.setEmail(normalizedEmail);

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setFirstName(
                request.firstName().trim()
        );

        user.setLastName(
                request.lastName().trim()
        );

        user.setRole(UserRole.CUSTOMER);

        user.setStatus(
                UserStatus.PENDING_VERIFICATION
        );

        user.setEmailVerified(false);

        User savedUser =
                userRepository.save(user);

        String rawToken =
                verificationTokenService
                        .createToken(savedUser);

        verificationDispatchService
                .sendVerificationToken(
                        savedUser.getEmail(),
                        rawToken
                );

        return savedUser;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(
            LoginRequest request
    ) {

        String normalizedEmail =
                request.email()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        User user =
                userRepository
                        .findByEmailIgnoreCase(
                                normalizedEmail
                        )
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid email or password"
                                )
                        );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (user.getStatus()
                != UserStatus.ACTIVE) {

            throw new AccountNotActiveException(
                    "Account is not active"
            );
        }

        if (!user.isEmailVerified()) {

            throw new AccountNotActiveException(
                    "Email verification is required"
            );
        }

        String token =
                jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional
    public void resendVerification(
            ResendVerificationRequest request
    ) {

        String normalizedEmail =
                request.email()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        User user =
                userRepository
                        .findByEmailIgnoreCase(
                                normalizedEmail
                        )
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Account not found"
                                )
                        );

        if (user.isEmailVerified()) {

            throw new EmailAlreadyVerifiedException(
                    "Email is already verified"
            );
        }

        String rawToken =
                verificationTokenService
                        .createToken(user);

        verificationDispatchService
                .sendVerificationToken(
                        user.getEmail(),
                        rawToken
                );
    }

    @Transactional
    public RegisterResponse verifyEmail(
            VerifyEmailRequest request
    ) {

        User user =
                verificationTokenService
                        .verifyToken(
                                request.token()
                        );

        return RegisterResponse.from(user);
    }
}