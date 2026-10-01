package com.fashionsense.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_CONFIRMED_TOPIC =
            "fashion-sense.order-confirmed";

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder
                .name(ORDER_CONFIRMED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}