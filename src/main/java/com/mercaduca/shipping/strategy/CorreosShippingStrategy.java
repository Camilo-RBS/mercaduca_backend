package com.mercaduca.shipping.strategy;

import com.mercaduca.common.enums.ShippingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class CorreosShippingStrategy implements ShippingStrategy {

    @Override
    public ShippingProvider getProvider() {
        return ShippingProvider.CORREOS;
    }

    @Override
    public ShippingQuote calculateShipping(ShippingRequest request) {
        log.info("Calculating Correos shipping quote");
        BigDecimal cost = BigDecimal.valueOf(5.50 + (request.getWeightKg() * 1.20));
        return ShippingQuote.builder()
                .provider(ShippingProvider.CORREOS)
                .cost(cost)
                .estimatedDays(7)
                .serviceType("Correos Ordinario")
                .build();
    }

    @Override
    public ShipmentResult createShipment(ShipmentCreationRequest request) {
        log.info("Creating Correos shipment for order: {}", request.getOrderNumber());
        String tracking = "ES" + UUID.randomUUID().toString().replace("-", "").substring(0, 13).toUpperCase() + "ES";
        return ShipmentResult.builder()
                .success(true)
                .trackingNumber(tracking)
                .shipmentId("CORREOS-" + request.getOrderId())
                .message("Correos shipment created")
                .build();
    }

    @Override
    public TrackingResult trackShipment(String trackingNumber) {
        return TrackingResult.builder()
                .trackingNumber(trackingNumber)
                .status("IN_TRANSIT")
                .location("Correos Sorting Center")
                .estimatedDelivery("5-7 business days")
                .build();
    }
}
