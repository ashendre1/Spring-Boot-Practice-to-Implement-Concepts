package com.practice.product.controller;

import com.practice.product.dto.ProductRequest;
import com.practice.product.dto.ProductResponse;
import com.practice.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product CRUD Operations")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    public Mono<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping
    @Operation(summary = "Get all products with backpressure (1 per second)")
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public Mono<ResponseEntity<ProductResponse>> getProductById(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product by ID")
    public Mono<ResponseEntity<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product by ID")
    public Mono<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @PutMapping("/{id}/reduce-stock")
    @Operation(summary = "Reduce stock for a product")
    public Mono<ResponseEntity<ProductResponse>> reduceStock(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Quantity to reduce", required = true, example = "5")
            @RequestParam("quantity") Integer quantity) {
        return productService.reduceStock(id, quantity)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
