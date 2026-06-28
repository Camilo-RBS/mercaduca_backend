package com.mercaduca.disputes.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.disputes.dto.DisputeDTOs;
import com.mercaduca.disputes.service.DisputeService;
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
@RequestMapping("/disputes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Disputas", description = "Gestión de disputas y reclamaciones")
public class DisputeController {

    private final DisputeService disputeService;

    // ── COMPRADOR ─────────────────────────────────────────────────────────────

    @PostMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('BUYER','SELLER')")
    @Operation(summary = "Abrir disputa para una orden (Comprador o Vendedor comprando)")
    public ResponseEntity<ApiResponse<DisputeDTOs.DisputeResponse>> open(
            @PathVariable Long orderId,
            @Valid @RequestBody DisputeDTOs.OpenDisputeRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Disputa abierta",
                        disputeService.openDispute(orderId, req, user.getId())));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('BUYER','SELLER')")
    @Operation(summary = "Ver mis disputas como comprador")
    public ResponseEntity<ApiResponse<PageResponse<DisputeDTOs.DisputeResponse>>> getMyDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                disputeService.getMyDisputes(user.getId(), PageRequest.of(page, size))));
    }

    // ── VENDEDOR ──────────────────────────────────────────────────────────────

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Ver disputas sobre mis productos (Vendedor)")
    public ResponseEntity<ApiResponse<PageResponse<DisputeDTOs.DisputeResponse>>> getSellerDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                disputeService.getSellerDisputes(user.getId(), PageRequest.of(page, size))));
    }

    @PatchMapping("/{id}/seller-response")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Responder a una disputa (Vendedor)")
    public ResponseEntity<ApiResponse<DisputeDTOs.DisputeResponse>> sellerRespond(
            @PathVariable Long id,
            @Valid @RequestBody DisputeDTOs.SellerResponseRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Respuesta enviada",
                disputeService.sellerRespond(id, req, user.getId())));
    }

    // ── ADMIN ─────────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ver todas las disputas (Admin)")
    public ResponseEntity<ApiResponse<PageResponse<DisputeDTOs.DisputeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                disputeService.getAllDisputes(PageRequest.of(page, size))));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resolver disputa (Admin)")
    public ResponseEntity<ApiResponse<DisputeDTOs.DisputeResponse>> resolve(
            @PathVariable Long id,
            @Valid @RequestBody DisputeDTOs.ResolveDisputeRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Disputa resuelta",
                disputeService.resolveDispute(id, req, user.getId())));
    }

    // ── COMPARTIDO ────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Ver disputa por ID")
    public ResponseEntity<ApiResponse<DisputeDTOs.DisputeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(disputeService.getDisputeById(id)));
    }
}
