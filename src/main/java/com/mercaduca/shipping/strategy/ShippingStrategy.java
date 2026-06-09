package com.mercaduca.shipping.strategy;

import com.mercaduca.common.enums.ShippingProvider;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Strategy Pattern: ShippingStrategy interface.
 * New providers can be added without modifying existing code (Open/Closed Principle).
 */
public interface ShippingStrategy {

    ShippingProvider getProvider();

    ShippingQuote calculateShipping(ShippingRequest request);

    ShipmentResult createShipment(ShipmentCreationRequest request);

    TrackingResult trackShipment(String trackingNumber);

    @Data
    @Builder
    class ShippingRequest {
        private String originAddress;
        private String originCity;
        private String originCountry;
        private String destinationAddress;
        private String destinationCity;
        private String destinationCountry;
        private String destinationZip;
        private Double weightKg;
        private Double lengthCm;
        private Double widthCm;
        private Double heightCm;
    }

    @Data
    @Builder
    class ShippingQuote {
        private ShippingProvider provider;
        private BigDecimal cost;
        private Integer estimatedDays;
        private String serviceType;
    }

    @Data
    @Builder
    class ShipmentCreationRequest {
        private ShippingRequest shippingRequest;
        private String recipientName;
        private String recipientPhone;
        private String recipientEmail;
        private Long orderId;
        private String orderNumber;
    }

    @Data
    @Builder
    class ShipmentResult {
        private boolean success;
        private String trackingNumber;
        private String shipmentId;
        private String message;
    }

    @Data
    @Builder
    class TrackingResult {
        private String trackingNumber;
        private String status;
        private String location;
        private String estimatedDelivery;
    }
}
