package com.ecommerce.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private  String status;
    private LocalDateTime createdAt;
}
