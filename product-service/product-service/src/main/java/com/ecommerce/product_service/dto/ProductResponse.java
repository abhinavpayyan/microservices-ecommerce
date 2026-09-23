package com.ecommerce.product_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String productName;
    private String description;
    private BigDecimal price;
    private Long stockQuantity;
}
