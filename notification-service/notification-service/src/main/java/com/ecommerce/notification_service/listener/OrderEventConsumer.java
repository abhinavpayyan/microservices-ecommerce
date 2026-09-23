package com.ecommerce.notification_service.listener;


import com.ecommerce.notification_service.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {


    @KafkaListener(topics = "order-created", groupId = "notification-service")
    public void handleOrderCreated(OrderCreatedEvent event){
        log.info("received order-created event: orderId={},productId={},quantity={},totalPrice={}",
                event.getId(),event.getProductId(),event.getQuantity(),event.getTotalPrice());

        log.info("order confirmation for order #{}",event.getId());
    }
}
