package com.ecommerce.order_service.service;

import com.ecommerce.order_service.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private static final String TOPIC="order-created";

    public void publishOrderCreatedEvent(OrderCreatedEvent createdEvent){
        kafkaTemplate.send(TOPIC, createdEvent);
    }
}
