package com.mercaduca.payments.strategy;

import com.mercaduca.common.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class StripePaymentStrategy implements PaymentStrategy {

    @Value("${app.payment.stripe.api-key:sk_test_placeholder}")
    private String apiKey;

    @Override
    public PaymentMethod getPaymentMethod() { return PaymentMethod.STRIPE; }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Stripe payment for order: {}", request.getOrderNumber());
        // In production: Use Stripe SDK to create a PaymentIntent
        // stripe.PaymentIntent.create(params);
        return PaymentResult.builder()
                .success(true)
                .paymentId("pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24))
                .status("SUCCEEDED")
                .message("Payment processed via Stripe")
                .build();
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        log.info("Refunding Stripe payment: {}", paymentId);
        return PaymentResult.builder()
                .success(true).paymentId(paymentId)
                .status("REFUNDED").message("Refund processed via Stripe")
                .build();
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentId) {
        return PaymentStatus.builder().paymentId(paymentId).status("SUCCEEDED").build();
    }
}
