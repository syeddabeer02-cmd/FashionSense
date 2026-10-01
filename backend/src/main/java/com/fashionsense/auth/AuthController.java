package com.fashionsense.auth;

import com.fashionsense.customer.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        User user =
                authService.register(request);

        return RegisterResponse.from(user);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        return authService.login(request);
    }

    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerification(
            @Valid
            @RequestBody
            ResendVerificationRequest request
    ) {

        authService.resendVerification(
                request
        );
    }

    @PostMapping("/verify-email")
    public RegisterResponse verifyEmail(
            @Valid
            @RequestBody
            VerifyEmailRequest request
    ) {

        return authService.verifyEmail(
                request
        );
    }
}