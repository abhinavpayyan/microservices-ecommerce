package com.ecommerce.product_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "product name is required")
    @Size(min = 3, max = 20, message = "product name must be between 3 and 20 characters")
    private String productName;

    @Positive(message = "price must be positive")
    private BigDecimal price;

    private String description;

    @PositiveOrZero(message = "this field is required,not be negative")
    private Long stockQuantity;

}
