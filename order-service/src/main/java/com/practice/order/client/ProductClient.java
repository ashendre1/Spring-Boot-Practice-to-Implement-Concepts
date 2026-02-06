package com.practice.order.client;

import com.practice.order.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * WebClient-based client for communicating with Product Service
 */
@Component
@Slf4j
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(@Value("${services.product.url}") String productServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    public Mono<ProductDTO> getProduct(Long productId) {
        return webClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .doOnError(e -> log.error("Error fetching product {}: {}", productId, e.getMessage()));
    }

    public Mono<Boolean> checkAvailability(Long productId, Integer quantity) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}/availability")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);
    }

    public Mono<ProductDTO> reduceStock(Long productId, Integer quantity) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}/reduce-stock")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .bodyToMono(ProductDTO.class);
    }
}
