package com.ecommerce.product_service.service;


import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;

import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
       Product product=Product.builder()
               .productName(request.getProductName())
               .price(request.getPrice())
               .description(request.getDescription())
               .stockQuantity(request.getStockQuantity())
               .build();
       Product saved=productRepository.save(product);
       return mapToResponse(saved);
    }

    public ProductResponse getProductById(Long id){
        Product product =productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found"));
        return mapToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    private ProductResponse mapToResponse(Product product){
        return new ProductResponse(product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());
    }
}

