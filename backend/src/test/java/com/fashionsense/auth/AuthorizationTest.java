package com.fashionsense.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationTest {
    @Autowired MockMvc mvc;
    @Autowired JwtEncoder encoder;

    private String token(String role) {
        return encoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
                .issuer("fashionsense").subject("security-test@example.test")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300))
                .claim("userId", 1L).claim("role", role).build())).getTokenValue();
    }

    @Test
    void healthIsPublicButApplicationInfoRequiresAuthentication() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/info")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/info").header("Authorization", "Bearer " + token("CUSTOMER")))
                .andExpect(status().isOk());
    }

    @Test
    void customerCannotWriteCatalogButAdminReachesRequestValidation() throws Exception {
        mvc.perform(post("/api/product-images").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/product-images").contentType("application/json").content("{}")
                .header("Authorization", "Bearer " + token("CUSTOMER"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/product-images").contentType("application/json").content("{}")
                .header("Authorization", "Bearer " + token("ADMIN"))).andExpect(status().isBadRequest());
    }

    @Test
    void customerCannotReadAdminInventory() throws Exception {
        mvc.perform(get("/api/variants/admin/product/1")
                .header("Authorization", "Bearer " + token("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidBearerAndAnonymousCustomerRequestsAreRejected() throws Exception {
        mvc.perform(get("/api/customers/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/info").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }
}
