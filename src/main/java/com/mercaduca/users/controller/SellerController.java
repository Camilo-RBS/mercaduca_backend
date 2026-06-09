package com.mercaduca.users.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.reports.dto.ReportDTOs;
import com.mercaduca.reports.service.ReportService;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints exclusivos para el rol SELLER sobre su propia tienda.
 * Separado de UserController para mantener el principio de responsabilidad única.
 */
@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vendedor", description = "Dashboard y reportes del vendedor")
public class SellerController {

    private final ReportService reportService;

    /**
     * Dashboard/reporte del vendedor autenticado.
     * Devuelve estadísticas de su propia tienda sin requerir rol ADMIN.
     */
    @GetMapping("/reports")
    @Operation(summary = "Obtener reporte/dashboard de mi tienda")
    public ResponseEntity<ApiResponse<ReportDTOs.SellerReport>> getMyReport(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success(reportService.getSellerReport(currentUser.getId())));
    }
}
