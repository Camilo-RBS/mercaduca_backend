package com.mercaduca.payments.strategy;

import com.mercaduca.common.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Strategy Pattern for payment processing.
 * Each payment provider implements this interface.
 */
public interface PaymentStrategy {

    PaymentMethod getPaymentMethod();

    PaymentResult processPayment(PaymentRequest request);

    PaymentResult refundPayment(String paymentId, BigDecimal amount);

    PaymentStatus getPaymentStatus(String paymentId);

    @Data
    @Builder
    class PaymentRequest {
        private String orderNumber;
        private BigDecimal amount;
        private String currency;
        private String description;
        private String customerEmail;
        private String paymentToken; // Stripe token, PayPal order ID, etc.
    }

    @Data
    @Builder
    class PaymentResult {
        private boolean success;
        private String paymentId;
        private String transactionId;
        private String status;
        private String message;
        private String redirectUrl; // For redirect-based flows (PayPal)
    }

    @Data
    @Builder
    class PaymentStatus {
        private String paymentId;
        private String status;
        private BigDecimal amount;
        private String currency;
    }
}
