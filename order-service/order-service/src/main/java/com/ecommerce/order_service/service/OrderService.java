package com.ecommerce.order_service.service;

import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.dto.ProductResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.event.OrderCreatedEvent;
import com.ecommerce.order_service.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventProducer eventProducer;
    private final ProductClient client;

    public OrderResponse placeOrder(OrderRequest request) {

        ProductResponse product;
        try {
            product = client.getProductById(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException("Product not found with id: " + request.getProductId());
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
        }

        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .build();

        Order saved = repository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getTotalPrice(),
                saved.getStatus()
        );
        eventProducer.publishOrderCreatedEvent(event);

        return mapToResponse(saved);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt());
    }
}
