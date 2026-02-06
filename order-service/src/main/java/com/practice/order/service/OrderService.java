package com.practice.order.service;

import com.practice.order.client.ProductClient;
import com.practice.order.dto.*;
import com.practice.order.model.Order;
import com.practice.order.model.OrderItem;
import com.practice.order.repository.OrderItemRepository;
import com.practice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Mono<OrderResponse> placeOrder(CreateOrderRequest request) {
        log.info("Processing order for customer {} with {} items",
                request.getCustomerId(), request.getItems().size());

        List<OrderResponse.OrderedItem> successfulItems = new ArrayList<>();
        List<OrderResponse.FailedItem> failedItems = new ArrayList<>();
        List<OrderItemRequest> successfulRequests = new ArrayList<>();

        // First, process all items and collect successful ones
        return Flux.fromIterable(request.getItems())
                .flatMap(item -> processOrderItem(item, successfulItems, failedItems, successfulRequests))
                .then(Mono.defer(() -> {
                    // If no successful items, return response without creating order
                    if (successfulItems.isEmpty()) {
                        return Mono.just(OrderResponse.builder()
                                .customerId(request.getCustomerId())
                                .customerEmail(request.getCustomerEmail())
                                .successfulItems(new ArrayList<>(successfulItems))
                                .failedItems(new ArrayList<>(failedItems))
                                .build());
                    }

                    // Create and save order
                    Order order = Order.builder()
                            .customerId(request.getCustomerId())
                            .customerEmail(request.getCustomerEmail())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                // Save order items
                                List<OrderItem> orderItems = successfulRequests.stream()
                                        .map(req -> OrderItem.builder()
                                                .orderId(savedOrder.getId())
                                                .productId(req.getProductId())
                                                .quantity(req.getQuantity())
                                                .build())
                                        .toList();

                                return Flux.fromIterable(orderItems)
                                        .flatMap(orderItemRepository::save)
                                        .then(Mono.just(savedOrder));
                            })
                            .map(savedOrder -> {
                                log.info("Order {} created successfully with {} items",
                                        savedOrder.getId(), successfulItems.size());
                                return OrderResponse.builder()
                                        .orderId(savedOrder.getId())
                                        .customerId(savedOrder.getCustomerId())
                                        .customerEmail(savedOrder.getCustomerEmail())
                                        .successfulItems(new ArrayList<>(successfulItems))
                                        .failedItems(new ArrayList<>(failedItems))
                                        .build();
                            });
                }));
    }

    private Mono<Void> processOrderItem(OrderItemRequest item,
                                         List<OrderResponse.OrderedItem> successfulItems,
                                         List<OrderResponse.FailedItem> failedItems,
                                         List<OrderItemRequest> successfulRequests) {
        return productClient.getProduct(item.getProductId())
                .flatMap(product -> {
                    if (product.getQuantity() >= item.getQuantity()) {
                        // Stock available - reduce it
                        return productClient.reduceStock(item.getProductId(), item.getQuantity())
                                .doOnSuccess(updatedProduct -> {
                                    synchronized (successfulItems) {
                                        successfulItems.add(OrderResponse.OrderedItem.builder()
                                                .productId(product.getId())
                                                .productName(product.getName())
                                                .quantity(item.getQuantity())
                                                .build());
                                        successfulRequests.add(item);
                                    }
                                    log.info("Successfully ordered {} x {} (ID: {})",
                                            item.getQuantity(), product.getName(), product.getId());
                                })
                                .then();
                    } else {
                        // Insufficient stock
                        synchronized (failedItems) {
                            failedItems.add(OrderResponse.FailedItem.builder()
                                    .productId(item.getProductId())
                                    .reason("Insufficient stock. Available: " + product.getQuantity() +
                                            ", Requested: " + item.getQuantity())
                                    .build());
                        }
                        log.warn("Insufficient stock for product {} (ID: {}). Available: {}, Requested: {}",
                                product.getName(), product.getId(), product.getQuantity(), item.getQuantity());
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    synchronized (failedItems) {
                        failedItems.add(OrderResponse.FailedItem.builder()
                                .productId(item.getProductId())
                                .reason("Product not found")
                                .build());
                    }
                    log.error("error {}", e);
                    return Mono.empty();
                });
    }
}


