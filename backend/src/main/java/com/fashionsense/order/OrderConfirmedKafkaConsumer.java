package com.fashionsense.order;

import com.fashionsense.config.KafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedKafkaConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OrderConfirmedKafkaConsumer.class
            );

    @KafkaListener(
            topics = KafkaTopicConfig.ORDER_CONFIRMED_TOPIC
    )
    public void consume(
            OrderConfirmedEvent event
    ) {

        LOGGER.info(
                "Consumed order-confirmed event: orderNumber={}, userId={}, totalAmount={}, shippingMethod={}, paymentMethod={}, occurredAt={}",
                event.orderNumber(),
                event.userId(),
                event.totalAmount(),
                event.shippingMethod(),
                event.paymentMethod(),
                event.occurredAt()
        );
    }
}