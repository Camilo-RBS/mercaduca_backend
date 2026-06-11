package com.mercaduca.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class CartDTOs {

    @Data
    public static class AddToCartRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    public static class UpdateCartItemRequest {
        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productTitle;
        private String productImage;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        private Integer availableStock;
        private Long sellerId;
        private String sellerName;
    }

    @Data
    public static class CartResponse {
        private List<CartItemResponse> items;
        private int itemCount;
        private BigDecimal total;
    }
}
