package com.ecommerce.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderCreatedEvent {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private  String status;
}
