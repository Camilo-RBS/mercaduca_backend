package com.mercaduca.orders.dto;

import com.mercaduca.common.enums.OrderStatus;
import com.mercaduca.common.enums.PaymentMethod;
import com.mercaduca.common.enums.ShippingProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTOs {

    @Data
    public static class CreateOrderRequest {
        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;

        private String paymentToken;

        @NotNull(message = "Shipping provider is required")
        private ShippingProvider shippingProvider;

        @NotBlank(message = "Shipping address is required")
        private String shippingAddress;

        @NotBlank(message = "Shipping city is required")
        private String shippingCity;

        @NotBlank(message = "Shipping country is required")
        private String shippingCountry;

        private String shippingZip;
        private String couponCode;
        private String notes;
    }

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productTitle;
        private String productImage;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        private Long sellerId;
        private String sellerName;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private Long buyerId;
        private String buyerName;
        private List<OrderItemResponse> items;
        private OrderStatus status;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal shippingCost;
        private BigDecimal total;
        private PaymentMethod paymentMethod;
        private String paymentId;
        private ShippingProvider shippingProvider;
        private String trackingNumber;
        private String shippingAddress;
        private String shippingCity;
        private String shippingCountry;
        private String couponCode;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class UpdateOrderStatusRequest {
        @NotNull
        private OrderStatus status;
        private String trackingNumber;
    }
}
