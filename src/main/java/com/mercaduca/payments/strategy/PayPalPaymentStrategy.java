package com.mercaduca.payments.strategy;

import com.mercaduca.common.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class PayPalPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getPaymentMethod() { return PaymentMethod.PAYPAL; }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing PayPal payment for order: {}", request.getOrderNumber());
        // In production: Create PayPal order and return approval URL
        String orderId = UUID.randomUUID().toString();
        return PaymentResult.builder()
                .success(true).paymentId(orderId)
                .status("APPROVED")
                .redirectUrl("https://www.sandbox.paypal.com/checkoutnow?token=" + orderId)
                .message("Redirect user to PayPal")
                .build();
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        return PaymentResult.builder().success(true).paymentId(paymentId)
                .status("REFUNDED").message("Refund processed via PayPal").build();
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentId) {
        return PaymentStatus.builder().paymentId(paymentId).status("COMPLETED").build();
    }
}
