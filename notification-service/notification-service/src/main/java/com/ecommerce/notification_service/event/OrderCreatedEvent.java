package com.ecommerce.notification_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
}
