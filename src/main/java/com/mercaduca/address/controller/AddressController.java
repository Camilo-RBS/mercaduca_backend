package com.mercaduca.address.controller;

import com.mercaduca.address.dto.AddressDTOs;
import com.mercaduca.address.service.AddressService;
import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController @RequestMapping("/addresses") @RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") @Tag(name = "Direcciones", description = "Direcciones de envío del usuario")
public class AddressController {
    private final AddressService addressService;
    @GetMapping @Operation(summary = "Ver mis direcciones")
    public ResponseEntity<ApiResponse<List<AddressDTOs.AddressResponse>>> getMyAddresses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getMyAddresses(user.getId()))); }
    @PostMapping @Operation(summary = "Agregar dirección")
    public ResponseEntity<ApiResponse<AddressDTOs.AddressResponse>> create(
            @Valid @RequestBody AddressDTOs.CreateAddressRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dirección guardada", addressService.createAddress(req, user.getId()))); }
    @PutMapping("/{id}") @Operation(summary = "Actualizar dirección")
    public ResponseEntity<ApiResponse<AddressDTOs.AddressResponse>> update(@PathVariable Long id,
            @Valid @RequestBody AddressDTOs.CreateAddressRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Dirección actualizada", addressService.updateAddress(id, req, user.getId()))); }
    @DeleteMapping("/{id}") @Operation(summary = "Eliminar dirección")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        addressService.deleteAddress(id, user.getId()); return ResponseEntity.ok(ApiResponse.success("Dirección eliminada", null)); }
    @PatchMapping("/{id}/default") @Operation(summary = "Marcar como dirección predeterminada")
    public ResponseEntity<ApiResponse<Void>> setDefault(@PathVariable Long id, @AuthenticationPrincipal User user) {
        addressService.setDefault(id, user.getId()); return ResponseEntity.ok(ApiResponse.success("Dirección predeterminada actualizada", null)); }
}
