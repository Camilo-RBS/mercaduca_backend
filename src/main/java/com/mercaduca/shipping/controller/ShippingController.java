package com.mercaduca.shipping.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.enums.ShippingProvider;
import com.mercaduca.shipping.service.ShippingService;
import com.mercaduca.shipping.strategy.ShippingStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Shipping", description = "Shipping quotes and tracking")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/quotes")
    @Operation(summary = "Get shipping quotes from all providers")
    public ResponseEntity<ApiResponse<List<ShippingStrategy.ShippingQuote>>> getQuotes(
            @RequestBody ShippingStrategy.ShippingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.getAllQuotes(request)));
    }

    @GetMapping("/track/{provider}/{trackingNumber}")
    @Operation(summary = "Track a shipment by provider and tracking number")
    public ResponseEntity<ApiResponse<ShippingStrategy.TrackingResult>> trackShipment(
            @PathVariable ShippingProvider provider,
            @PathVariable String trackingNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                shippingService.trackShipment(provider, trackingNumber)));
    }
}
