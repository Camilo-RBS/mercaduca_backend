package com.mercaduca.shipping.strategy;

import com.mercaduca.common.enums.ShippingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class UberDirectShippingStrategy implements ShippingStrategy {

    @Override
    public ShippingProvider getProvider() {
        return ShippingProvider.UBER_DIRECT;
    }

    @Override
    public ShippingQuote calculateShipping(ShippingRequest request) {
        log.info("Calculating Uber Direct quote");
        // Uber Direct: same-day local delivery
        BigDecimal cost = BigDecimal.valueOf(8.99 + (request.getWeightKg() * 0.50));
        return ShippingQuote.builder()
                .provider(ShippingProvider.UBER_DIRECT)
                .cost(cost)
                .estimatedDays(1)
                .serviceType("Same-Day Delivery")
                .build();
    }

    @Override
    public ShipmentResult createShipment(ShipmentCreationRequest request) {
        String deliveryId = "UD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ShipmentResult.builder()
                .success(true)
                .trackingNumber(deliveryId)
                .shipmentId(deliveryId)
                .message("Uber Direct delivery scheduled")
                .build();
    }

    @Override
    public TrackingResult trackShipment(String trackingNumber) {
        return TrackingResult.builder()
                .trackingNumber(trackingNumber)
                .status("DRIVER_ASSIGNED")
                .location("Pickup in progress")
                .estimatedDelivery("Today, 2-4 hours")
                .build();
    }
}
