package com.mercaduca.shipping.service;

import com.mercaduca.common.enums.ShippingProvider;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.shipping.strategy.ShippingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShippingService {

    private final Map<ShippingProvider, ShippingStrategy> strategies;

    @Autowired
    public ShippingService(List<ShippingStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ShippingStrategy::getProvider, Function.identity()));
    }

    public ShippingStrategy getStrategy(ShippingProvider provider) {
        ShippingStrategy strategy = strategies.get(provider);
        if (strategy == null) {
            throw new BusinessException("Shipping provider not supported: " + provider);
        }
        return strategy;
    }

    public List<ShippingStrategy.ShippingQuote> getAllQuotes(ShippingStrategy.ShippingRequest request) {
        return strategies.values().stream()
                .map(strategy -> strategy.calculateShipping(request))
                .collect(Collectors.toList());
    }

    public ShippingStrategy.ShipmentResult createShipment(
            ShippingProvider provider, ShippingStrategy.ShipmentCreationRequest request) {
        return getStrategy(provider).createShipment(request);
    }

    public ShippingStrategy.TrackingResult trackShipment(ShippingProvider provider, String trackingNumber) {
        return getStrategy(provider).trackShipment(trackingNumber);
    }
}