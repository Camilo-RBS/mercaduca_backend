package com.mercaduca.payments.service;

import com.mercaduca.common.enums.PaymentMethod;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.payments.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final Map<PaymentMethod, PaymentStrategy> strategies;

    @Autowired
    public PaymentService(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getPaymentMethod, Function.identity()));
    }

    public PaymentStrategy.PaymentResult processPayment(PaymentMethod method,
                                                        PaymentStrategy.PaymentRequest request) {
        return getStrategy(method).processPayment(request);
    }

    public PaymentStrategy.PaymentResult refundPayment(PaymentMethod method,
                                                       String paymentId, BigDecimal amount) {
        return getStrategy(method).refundPayment(paymentId, amount);
    }

    public PaymentStrategy.PaymentStatus getStatus(PaymentMethod method, String paymentId) {
        return getStrategy(method).getPaymentStatus(paymentId);
    }

    private PaymentStrategy getStrategy(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new BusinessException("Payment method not supported: " + method);
        }
        return strategy;
    }
}