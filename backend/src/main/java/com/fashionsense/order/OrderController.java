package com.fashionsense.order;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/me/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> getOrders(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return orderService.getOrders(
                getUserId(jwt)
        );
    }

    @GetMapping("/{orderNumber}")
    public OrderResponse getOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String orderNumber
    ) {

        return orderService.getOrder(
                getUserId(jwt),
                orderNumber
        );
    }

    private Long getUserId(
            Jwt jwt
    ) {

        Number userId =
                jwt.getClaim("userId");

        return userId.longValue();
    }
}