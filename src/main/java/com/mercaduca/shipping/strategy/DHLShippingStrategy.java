package com.mercaduca.shipping.strategy;

import com.mercaduca.common.enums.ShippingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class DHLShippingStrategy implements ShippingStrategy {

    @Value("${app.shipping.dhl.api-key:placeholder}")
    private String apiKey;

    @Value("${app.shipping.dhl.base-url:https://api-mock.dhl.com/mydhlapi}")
    private String baseUrl;

    @Override
    public ShippingProvider getProvider() {
        return ShippingProvider.DHL;
    }

    @Override
    public ShippingQuote calculateShipping(ShippingRequest request) {
        log.info("Calculating DHL shipping from {} to {}", request.getOriginCity(), request.getDestinationCity());
        // In production: call DHL API to get real quote
        BigDecimal baseCost = BigDecimal.valueOf(15.00);
        BigDecimal weightSurcharge = BigDecimal.valueOf(request.getWeightKg() * 2.5);

        return ShippingQuote.builder()
                .provider(ShippingProvider.DHL)
                .cost(baseCost.add(weightSurcharge))
                .estimatedDays(3)
                .serviceType("DHL Express")
                .build();
    }

    @Override
    public ShipmentResult createShipment(ShipmentCreationRequest request) {
        log.info("Creating DHL shipment for order: {}", request.getOrderNumber());
        // In production: call DHL createShipment API
        String trackingNumber = "DHL" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        return ShipmentResult.builder()
                .success(true)
                .trackingNumber(trackingNumber)
                .shipmentId("DHL-SHP-" + request.getOrderId())
                .message("DHL shipment created successfully")
                .build();
    }

    @Override
    public TrackingResult trackShipment(String trackingNumber) {
        log.info("Tracking DHL shipment: {}", trackingNumber);
        // In production: call DHL tracking API
        return TrackingResult.builder()
                .trackingNumber(trackingNumber)
                .status("IN_TRANSIT")
                .location("DHL Distribution Center")
                .estimatedDelivery("3 business days")
                .build();
    }
}
