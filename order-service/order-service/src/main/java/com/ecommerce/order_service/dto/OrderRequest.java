package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NonNull;

@Data
public class OrderRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be positive")
    private Integer quantity;
}
