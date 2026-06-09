package com.mercaduca.orders.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.orders.dto.OrderDTOs;
import com.mercaduca.orders.service.OrderService;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUYER','SELLER')")
    @Operation(summary = "Create order from cart (Buyer or Seller buying)")
    public ResponseEntity<ApiResponse<OrderDTOs.OrderResponse>> createOrder(
            @Valid @RequestBody OrderDTOs.CreateOrderRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created", orderService.createOrder(request, user.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderDTOs.OrderResponse>> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, user.getId())));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<ApiResponse<OrderDTOs.OrderResponse>> getOrderByNumber(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getOrderByNumber(orderNumber, user.getId())));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('BUYER','SELLER')")
    @Operation(summary = "Get user's purchase history")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTOs.OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getBuyerOrders(user.getId(), PageRequest.of(page, size))));
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Get seller's received orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTOs.OrderResponse>>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getSellerOrders(user.getId(), PageRequest.of(page, size))));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Update order status (Seller / Admin)")
    public ResponseEntity<ApiResponse<OrderDTOs.OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderDTOs.UpdateOrderStatusRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                orderService.updateOrderStatus(id, request, user.getId())));
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('BUYER','SELLER')")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        orderService.cancelOrder(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", null));
    }
}
