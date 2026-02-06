package com.practice.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to place an order")
public class CreateOrderRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer ID", example = "1")
    private Long customerId;

    @NotNull(message = "Customer email is required")
    @Email(message = "Valid email is required")
    @Schema(description = "Customer email", example = "customer@example.com")
    private String customerEmail;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    @Schema(description = "List of items to order")
    private List<OrderItemRequest> items;
}
