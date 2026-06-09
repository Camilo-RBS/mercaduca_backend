package com.mercaduca.users.controller;

import com.mercaduca.auth.dto.AuthDTOs;
import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.users.dto.UserDTOs;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.service.UserService;
import com.mercaduca.warnings.dto.WarningDTOs;
import com.mercaduca.warnings.service.WarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestion de perfiles y administracion")
public class UserController {
    private final UserService userService;
    private final WarningService warningService;

    /**
     * Busqueda de usuarios por nombre, username o email.
     * Accesible a cualquier usuario autenticado (BUYER, SELLER, ADMIN).
     * Usado principalmente por el chat para encontrar con quien chatear.
     */
    @GetMapping("/users/search")
    @Operation(summary = "Buscar usuarios por nombre/username/email")
    public ResponseEntity<ApiResponse<List<UserDTOs.UserSearchResult>>> searchUsers(
            @RequestParam String keyword,
            @AuthenticationPrincipal User currentUser) {
        Long excludeId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(userService.searchUsers(keyword, excludeId)));
    }

    @GetMapping("/users/me")
    @Operation(summary = "Ver mi perfil")
    public ResponseEntity<ApiResponse<UserDTOs.ProfileResponse>> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(user.getId())));
    }

    @PutMapping("/users/me")
    @Operation(summary = "Actualizar mi perfil")
    public ResponseEntity<ApiResponse<UserDTOs.ProfileResponse>> updateProfile(
            @Valid @RequestBody UserDTOs.UpdateProfileRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado", userService.updateProfile(user.getId(), request)));
    }

    @PatchMapping("/users/me/password")
    @Operation(summary = "Cambiar contrasena")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody UserDTOs.ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Contrasena actualizada", null));
    }

    @PostMapping("/sellers/register")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER')")
    @Operation(summary = "Solicitar convertirse en vendedor (BUYER) o completar datos de tienda (SELLER)")
    public ResponseEntity<ApiResponse<UserDTOs.SellerApprovalResponse>> registerAsSeller(
            @Valid @RequestBody AuthDTOs.SellerRegistrationRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Registro enviado", userService.registerAsSeller(request, user.getId())));
    }

    @GetMapping("/admin/sellers/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vendedores pendientes de aprobacion")
    public ResponseEntity<ApiResponse<PageResponse<UserDTOs.PendingSellerResponse>>> getPendingSellers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(userService.getPendingSellers(PageRequest.of(page, size))));
    }

    @PatchMapping("/admin/sellers/{sellerId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aprobar vendedor")
    public ResponseEntity<ApiResponse<UserDTOs.SellerApprovalResponse>> approveSeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success("Vendedor aprobado", userService.approveSeller(sellerId)));
    }

    @PatchMapping("/admin/sellers/{sellerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechazar vendedor")
    public ResponseEntity<ApiResponse<UserDTOs.SellerApprovalResponse>> rejectSeller(
            @PathVariable Long sellerId, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success("Vendedor rechazado", userService.rejectSeller(sellerId, reason)));
    }

    @GetMapping("/admin/sellers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Todos los vendedores con estadísticas")
    public ResponseEntity<ApiResponse<PageResponse<UserDTOs.SellerAdminResponse>>> getAllSellers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllSellers(PageRequest.of(page, size))));
    }

    @PatchMapping("/admin/sellers/{sellerId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspender vendedor")
    public ResponseEntity<ApiResponse<UserDTOs.SellerApprovalResponse>> suspendSeller(
            @PathVariable Long sellerId, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success("Vendedor suspendido",
                userService.suspendSeller(sellerId, reason)));
    }

    @PatchMapping("/admin/sellers/{sellerId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloquear vendedor")
    public ResponseEntity<ApiResponse<UserDTOs.SellerApprovalResponse>> blockSeller(
            @PathVariable Long sellerId, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success("Vendedor bloqueado",
                userService.blockSeller(sellerId, reason)));
    }

    @PatchMapping("/admin/sellers/{sellerId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desbloquear vendedor")
    public ResponseEntity<ApiResponse<UserDTOs.SellerApprovalResponse>> unblockSeller(
            @PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success("Vendedor desbloqueado",
                userService.unblockSeller(sellerId)));
    }

    @PostMapping("/admin/users/{sellerId}/warn")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar advertencia a un vendedor")
    public ResponseEntity<ApiResponse<WarningDTOs.WarningResponse>> warnSeller(
            @PathVariable Long sellerId,
            @Valid @RequestBody WarningDTOs.IssueWarningRequest req,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(ApiResponse.success("Advertencia enviada",
                warningService.issueWarning(sellerId, admin.getId(), req)));
    }

    @GetMapping("/admin/users/{sellerId}/warnings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ver advertencias de un vendedor")
    public ResponseEntity<ApiResponse<List<WarningDTOs.WarningResponse>>> getWarnings(
            @PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success(warningService.getSellerWarnings(sellerId)));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Todos los usuarios del sistema")
    public ResponseEntity<ApiResponse<PageResponse<UserDTOs.AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(PageRequest.of(page, size))));
    }

    @PatchMapping("/admin/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar o desactivar usuario")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long userId) {
        userService.toggleUserStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", null));
    }

    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ver perfil de cualquier usuario")
    public ResponseEntity<ApiResponse<UserDTOs.ProfileResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userId)));
    }
}
