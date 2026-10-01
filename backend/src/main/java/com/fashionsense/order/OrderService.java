package com.fashionsense.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(
            Long userId
    ) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(order -> {

                    List<OrderItem> items =
                            orderItemRepository
                                    .findByOrderIdOrderByIdAsc(
                                            order.getId()
                                    );

                    return OrderResponse.from(
                            order,
                            items
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(
            Long userId,
            String orderNumber
    ) {

        CustomerOrder order =
                orderRepository
                        .findByOrderNumberAndUserId(
                                orderNumber,
                                userId
                        )
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found: "
                                                + orderNumber
                                )
                        );

        List<OrderItem> items =
                orderItemRepository
                        .findByOrderIdOrderByIdAsc(
                                order.getId()
                        );

        return OrderResponse.from(
                order,
                items
        );
    }
}