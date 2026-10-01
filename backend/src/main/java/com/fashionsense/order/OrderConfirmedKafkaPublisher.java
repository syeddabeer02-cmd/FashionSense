package com.fashionsense.order;

import com.fashionsense.config.KafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderConfirmedKafkaPublisher {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OrderConfirmedKafkaPublisher.class
            );

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public OrderConfirmedKafkaPublisher(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void publish(
            OrderConfirmedEvent event
    ) {

        kafkaTemplate
                .send(
                        KafkaTopicConfig.ORDER_CONFIRMED_TOPIC,
                        event.orderNumber(),
                        event
                )
                .whenComplete(
                        (result, exception) -> {

                            if (exception != null) {

                                LOGGER.error(
                                        "Failed to publish order-confirmed event for order {}",
                                        event.orderNumber(),
                                        exception
                                );

                                return;
                            }

                            LOGGER.info(
                                    "Published order-confirmed event: orderNumber={}, partition={}, offset={}",
                                    event.orderNumber(),
                                    result.getRecordMetadata()
                                            .partition(),
                                    result.getRecordMetadata()
                                            .offset()
                            );
                        }
                );
    }
}