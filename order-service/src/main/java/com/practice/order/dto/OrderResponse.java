package com.practice.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order placement response")
public class OrderResponse {

    @Schema(description = "Order ID (only present if at least one item was successful)", example = "1")
    private Long orderId;

    @Schema(description = "Customer ID", example = "1")
    private Long customerId;

    @Schema(description = "Customer email", example = "customer@example.com")
    private String customerEmail;

    @Schema(description = "List of successfully ordered items")
    private List<OrderedItem> successfulItems;

    @Schema(description = "List of failed items (insufficient stock)")
    private List<FailedItem> failedItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Successfully ordered item")
    public static class OrderedItem {
        @Schema(description = "Product ID", example = "1")
        private Long productId;

        @Schema(description = "Product name", example = "Laptop")
        private String productName;

        @Schema(description = "Quantity ordered", example = "2")
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Failed order item")
    public static class FailedItem {
        @Schema(description = "Product ID", example = "2")
        private Long productId;

        @Schema(description = "Reason for failure", example = "Insufficient stock")
        private String reason;
    }
}
