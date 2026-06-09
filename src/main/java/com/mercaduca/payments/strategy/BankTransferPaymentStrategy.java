package com.mercaduca.payments.strategy;

import com.mercaduca.common.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class BankTransferPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getPaymentMethod() { return PaymentMethod.BANK_TRANSFER; }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        String reference = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return PaymentResult.builder()
                .success(true).paymentId(reference)
                .status("PENDING_CONFIRMATION")
                .message("Transfer reference: " + reference + ". Please complete bank transfer.")
                .build();
    }

    @Override
    public PaymentResult refundPayment(String paymentId, BigDecimal amount) {
        return PaymentResult.builder().success(true).paymentId(paymentId)
                .status("REFUND_PENDING").message("Refund will be processed in 3-5 business days").build();
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentId) {
        return PaymentStatus.builder().paymentId(paymentId).status("PENDING_CONFIRMATION").build();
    }
}
