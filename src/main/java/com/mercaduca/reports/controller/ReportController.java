package com.mercaduca.reports.controller;
import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.common.enums.OrderStatus;
import com.mercaduca.orders.dto.OrderDTOs;
import com.mercaduca.orders.service.OrderService;
import com.mercaduca.reports.dto.ReportDTOs;
import com.mercaduca.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/admin") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") @SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administración", description = "Panel de control y reportes para administradores")
public class ReportController {
    private final ReportService reportService;
    private final OrderService orderService;
    @GetMapping("/reports/dashboard") @Operation(summary = "Dashboard general de la plataforma")
    public ResponseEntity<ApiResponse<ReportDTOs.DashboardReport>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDashboard())); }
    @GetMapping("/reports/sellers/{sellerId}") @Operation(summary = "Reporte de un vendedor específico")
    public ResponseEntity<ApiResponse<ReportDTOs.SellerReport>> getSellerReport(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSellerReport(sellerId))); }
    @GetMapping("/orders") @Operation(summary = "Todas las órdenes de la plataforma")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTOs.OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(PageRequest.of(page, size)))); }
    @GetMapping("/orders/status/{status}") @Operation(summary = "Órdenes filtradas por estado")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTOs.OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByStatus(status, PageRequest.of(page, size)))); }
}
