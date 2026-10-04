package com.fashionsense.integration;

import com.fashionsense.auth.verification.VerificationDispatchService;
import com.fashionsense.promotion.*;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerJourneyTest {
    @Autowired MockMvc mvc;
    @Autowired JwtEncoder encoder;
    @Autowired JdbcTemplate jdbc;
    @Autowired PromotionRepository promotions;
    @MockitoBean VerificationDispatchService dispatch;
    private static final String PASSWORD = "Test-password-123";
    private static final String CART = "/api/customers/me/cart";
    private static final String ADDRESSES = "/api/customers/me/addresses";

    private String request(MockHttpServletRequestBuilder request, String token, int expected) throws Exception {
        if (token != null) request.header("Authorization", "Bearer " + token);
        return mvc.perform(request).andExpect(status().is(expected)).andReturn().getResponse().getContentAsString();
    }
    private MockHttpServletRequestBuilder body(MockHttpServletRequestBuilder request, String json) {
        return request.contentType("application/json").content(json);
    }
    private Object value(String json, String path) { return JsonPath.read(json, path); }
    private long id(String json) { return ((Number) value(json, "$.id")).longValue(); }
    private void amount(String json, String path, String expected) {
        assertEquals(0, new BigDecimal(expected).compareTo(new BigDecimal(value(json, path).toString())));
    }
    private String admin() {
        return encoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder().issuer("fashionsense")
                .subject("admin-test@example.test").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300))
                .claim("userId", 1L).claim("role", "ADMIN").build())).getTokenValue();
    }
    private String register(String email) throws Exception {
        return request(body(post("/api/auth/register"), """
                {"email":"%s","password":"%s","firstName":"Test","lastName":"Customer"}
                """.formatted(email, PASSWORD)), null, 201);
    }
    private String login(String email, String password, int expected) throws Exception {
        return request(body(post("/api/auth/login"), """
                {"email":"%s","password":"%s"}
                """.formatted(email, password)), null, expected);
    }
    private String verificationToken(String email, int sends) {
        var capture = ArgumentCaptor.forClass(String.class);
        verify(dispatch, times(sends)).sendVerificationToken(eq(email), capture.capture());
        return capture.getValue();
    }
    private String customer() throws Exception {
        String email = "customer-" + UUID.randomUUID() + "@example.test";
        register(email);
        request(body(post("/api/auth/verify-email"), "{\"token\":\"" + verificationToken(email, 1) + "\"}"), null, 200);
        return (String) value(login(email, PASSWORD, 200), "$.accessToken");
    }
    private long address(String token, boolean isDefault) throws Exception {
        return id(request(body(post(ADDRESSES), """
                {"label":"Home","recipientName":"Test Customer","addressLine1":"123 Test Street",
                "city":"Richardson","state":"TX","postalCode":"75080","countryCode":"US","defaultAddress":%s}
                """.formatted(isDefault)), token, 201));
    }
    private record Catalog(long product, long variant, long brand, long category, String slug, String sku) { }
    private Catalog catalog(int stock) throws Exception {
        String suffix = UUID.randomUUID().toString(), token = admin();
        long brand = id(request(body(post("/api/brands"), "{\"name\":\"Test Brand\",\"slug\":\"brand-" + suffix + "\"}"), token, 201));
        long category = id(request(body(post("/api/categories"), "{\"name\":\"Test Category\",\"slug\":\"category-" + suffix + "\"}"), token, 201));
        String slug = "product-" + suffix, sku = "SKU-" + suffix;
        long product = id(request(body(post("/api/products"), """
                {"brandId":%d,"categoryId":%d,"name":"Test Shirt","slug":"%s","basePrice":100.00}
                """.formatted(brand, category, slug)), token, 201));
        long variant = id(request(body(post("/api/variants"), """
                {"productId":%d,"sku":"%s","size":"M","color":"Green","style":"Casual","material":"Cotton","stockQuantity":%d}
                """.formatted(product, sku, stock)), token, 201));
        return new Catalog(product, variant, brand, category, slug, sku);
    }
    private String add(String token, Catalog c, int quantity, int expected) throws Exception {
        return request(body(post(CART + "/items"), "{\"sku\":\"" + c.sku + "\",\"quantity\":" + quantity + "}"), token, expected);
    }

    @Test
    void registrationVerificationResendAndLoginEnforceAccountState() throws Exception {
        String email = "Customer-" + UUID.randomUUID() + "@Example.Test", normalized = email.toLowerCase(java.util.Locale.ROOT);
        String registered = register(email);
        assertEquals(normalized, value(registered, "$.email"));
        login(normalized, PASSWORD, 403);
        String original = verificationToken(normalized, 1);
        request(body(post("/api/auth/resend-verification"), "{\"email\":\"" + email + "\"}"), null, 204);
        String replacement = verificationToken(normalized, 2);
        assertNotEquals(original, replacement);
        request(body(post("/api/auth/verify-email"), "{\"token\":\"" + replacement + "\"}"), null, 200);
        request(body(post("/api/auth/verify-email"), "{\"token\":\"" + original + "\"}"), null, 400);
        request(body(post("/api/auth/verify-email"), "{\"token\":\"" + replacement + "\"}"), null, 400);
        String token = (String) value(login(email, PASSWORD, 200), "$.accessToken");
        assertEquals(normalized, value(request(get("/api/customers/me"), token, 200), "$.email"));
        login(email, "wrong-password", 401);
        registerDuplicate(email);
        request(body(post("/api/auth/resend-verification"), "{\"email\":\"" + email + "\"}"), null, 409);
    }
    private void registerDuplicate(String email) throws Exception {
        request(body(post("/api/auth/register"), """
                {"email":"%s","password":"%s","firstName":"Test","lastName":"Customer"}
                """.formatted(email, PASSWORD)), null, 409);
    }

    @Test
    void checkoutAppliesPromotionAndReplayDoesNotCreateAnotherOrderOrDecrementStock() throws Exception {
        String token = customer(); Catalog c = catalog(5); long address = address(token, true);
        add(token, c, 2, 200);
        Promotion p = new Promotion(); p.setName("Journey Discount"); p.setCode("J" + UUID.randomUUID().toString().replace("-", ""));
        p.setScope(PromotionScope.CART); p.setType(PromotionType.PERCENTAGE); p.setPercentage(new BigDecimal("10"));
        promotions.saveAndFlush(p);
        String payload = """
                {"addressId":%d,"shippingMethod":"STANDARD","paymentMethod":"CARD","promotionCode":"%s"}
                """.formatted(address, p.getCode());
        String key = UUID.randomUUID().toString();
        String order = request(body(post("/api/customers/me/checkout").header("Idempotency-Key", key), payload), token, 201);
        amount(order, "$.subtotal", "200.00"); amount(order, "$.discountAmount", "20.00");
        amount(order, "$.shippingAmount", "0.00"); amount(order, "$.taxAmount", "14.85"); amount(order, "$.totalAmount", "194.85");
        assertEquals("SUCCEEDED", value(order, "$.paymentStatus"));
        assertEquals(3, jdbc.queryForObject("SELECT stock_quantity FROM product_variants WHERE id=?", Integer.class, c.variant));
        String replay = request(body(post("/api/customers/me/checkout").header("Idempotency-Key", key), payload), token, 201);
        assertEquals(value(order, "$.orderNumber"), value(replay, "$.orderNumber"));
        assertEquals(3, jdbc.queryForObject("SELECT stock_quantity FROM product_variants WHERE id=?", Integer.class, c.variant));
        assertEquals(1, ((java.util.List<?>) value(request(get("/api/customers/me/orders"), token, 200), "$")).size());
        assertTrue(((java.util.List<?>) value(request(get(CART), token, 200), "$.items")).isEmpty());
        String number = (String) value(order, "$.orderNumber");
        request(get("/api/customers/me/orders/" + number), customer(), 404);
        request(delete(ADDRESSES + "/" + address), token, 204);
        assertEquals("123 Test Street", value(request(get("/api/customers/me/orders/" + number), token, 200), "$.addressLine1"));
    }

    @Test
    void wishlistAndCartAreIdempotentAndRejectAnotherCustomersItem() throws Exception {
        String token = customer(), other = customer(); Catalog c = catalog(5);
        request(put("/api/customers/me/wishlist/" + c.slug), token, 200);
        request(put("/api/customers/me/wishlist/" + c.slug), token, 200);
        assertEquals(1, ((java.util.List<?>) value(request(get("/api/customers/me/wishlist"), token, 200), "$")).size());
        request(delete("/api/customers/me/wishlist/" + c.slug), token, 204);
        String cart = add(token, c, 1, 200); add(token, c, 1, 200);
        long item = ((Number) value(cart, "$.items[0].cartItemId")).longValue();
        String updated = request(body(put(CART + "/items/" + item), "{\"quantity\":3}"), token, 200);
        amount(updated, "$.subtotal", "300.00");
        request(body(put(CART + "/items/" + item), "{\"quantity\":1}"), other, 404);
        request(delete(CART + "/items/" + item), other, 404);
        request(delete(CART + "/items/" + item), token, 200);
        assertTrue(((java.util.List<?>) value(request(get(CART), token, 200), "$.items")).isEmpty());
        add(token, c, 6, 409);
    }

    @Test
    void addressDefaultChangesAndOwnershipAreEnforced() throws Exception {
        String token = customer(), other = customer(); long first = address(token, true), second = address(token, false);
        request(put(ADDRESSES + "/" + second + "/default"), token, 200);
        String list = request(get(ADDRESSES), token, 200);
        assertEquals(1, ((java.util.List<?>) value(list, "$[?(@.defaultAddress == true)]")).size());
        request(put(ADDRESSES + "/" + first + "/default"), other, 404);
        request(delete(ADDRESSES + "/" + first), other, 404);
        request(delete(ADDRESSES + "/" + first), token, 204);
        request(delete(ADDRESSES + "/" + second), token, 204);
        assertTrue(((java.util.List<?>) value(request(get(ADDRESSES), token, 200), "$")).isEmpty());
    }

    @Test
    void publicCatalogFiltersAndStockResponsesHideExactQuantities() throws Exception {
        Catalog c = catalog(5);
        String filtered = request(get("/api/products").param("size", "M").param("color", "Green")
                .param("minPrice", "100").param("maxPrice", "100").param("brand", "brand-" + c.slug.substring("product-".length())), null, 200);
        assertTrue(((java.util.List<?>) value(filtered, "$.content[*].slug")).contains(c.slug));
        String variants = request(get("/api/variants/product/" + c.product), null, 200);
        assertFalse(variants.contains("stockQuantity"));
        assertEquals("IN_STOCK", value(variants, "$[0].availability"));
        request(get("/api/products/" + c.slug + "/details"), null, 200);
        request(body(put("/api/variants/" + c.variant + "/stock"), "{\"stockQuantity\":0}"), admin(), 200);
        assertEquals("OUT_OF_STOCK", value(request(get("/api/variants/sku/" + c.sku), null, 200), "$.availability"));
        add(customer(), c, 1, 409);
        request(get("/api/products/does-not-exist"), null, 404);
    }

    @Test
    void imageMetadataAndProductUpdatesRequireValidCatalogState() throws Exception {
        Catalog c = catalog(2); String admin = admin();
        String payload = """
                {"productId":%d,"imageUrl":"https://images.example.test/shirt.png","altText":"Shirt","displayOrder":0,"primaryImage":true}
                """.formatted(c.product);
        long image = id(request(body(post("/api/product-images"), payload), admin, 201));
        request(get("/api/product-images/product/" + c.product), null, 200);
        request(body(post("/api/product-images"), payload), admin, 409);
        request(delete("/api/product-images/" + image), admin, 204);
        assertTrue(((java.util.List<?>) value(request(get("/api/product-images/product/" + c.product), null, 200), "$")).isEmpty());
        String updated = request(body(put("/api/products/" + c.product), """
                {"brandId":%d,"categoryId":%d,"name":"Updated Shirt","basePrice":120.00,"active":true}
                """.formatted(c.brand, c.category)), admin, 200);
        assertEquals("Updated Shirt", value(updated, "$.name"));
        amount(updated, "$.basePrice", "120.00");
    }

    @Test
    void invalidRequestsAndEmptyCheckoutAreRejected() throws Exception {
        request(body(post("/api/auth/register"), "{\"email\":\"invalid\",\"password\":\"short\"}"), null, 400);
        request(body(post("/api/auth/verify-email"), "{\"token\":\"unknown\"}"), null, 400);
        request(get("/api/products").param("page", "-10").param("pageSize", "1000").param("sortBy", "untrusted"), null, 200);
        String token = customer(); long address = address(token, true);
        request(body(post(CART + "/items"), "{\"sku\":\"missing\",\"quantity\":0}"), token, 400);
        request(body(post("/api/customers/me/checkout").header("Idempotency-Key", " "), """
                {"addressId":%d,"shippingMethod":"STANDARD","paymentMethod":"CARD"}
                """.formatted(address)), token, 400);
        request(body(post("/api/customers/me/checkout").header("Idempotency-Key", "x".repeat(101)), """
                {"addressId":%d,"shippingMethod":"STANDARD","paymentMethod":"CARD"}
                """.formatted(address)), token, 400);
        request(body(post("/api/customers/me/checkout").header("Idempotency-Key", UUID.randomUUID().toString()), """
                {"addressId":%d,"shippingMethod":"STANDARD","paymentMethod":"CARD"}
                """.formatted(address)), token, 400);
    }
}
