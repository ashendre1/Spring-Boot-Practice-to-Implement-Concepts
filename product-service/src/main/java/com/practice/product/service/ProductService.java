package com.practice.product.service;

import com.practice.product.dto.ProductRequest;
import com.practice.product.dto.ProductResponse;
import com.practice.product.model.Product;
import com.practice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Mono<ProductResponse> createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return productRepository.save(product)
                .map(this::mapToResponse);
    }

    public Mono<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToResponse);
    }

    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .delayElements(Duration.ofSeconds(1))
                .map(this::mapToResponse);
    }

    public Mono<ProductResponse> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setDescription(request.getDescription());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setQuantity(request.getQuantity());
                    existingProduct.setCategory(request.getCategory());
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(existingProduct);
                })
                .map(this::mapToResponse);
    }

    public Mono<Void> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

    public Mono<ProductResponse> reduceStock(Long id, Integer quantity) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    if (product.getQuantity() >= quantity) {
                        product.setQuantity(product.getQuantity() - quantity);
                        product.setUpdatedAt(LocalDateTime.now());
                        return productRepository.save(product).map(this::mapToResponse);
                    } else {
                        return Mono.empty();
                    }
                });
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
