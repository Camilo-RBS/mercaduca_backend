package com.mercaduca.wishlist.controller;
import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.users.entity.User;
import com.mercaduca.wishlist.dto.WishlistDTOs;
import com.mercaduca.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/wishlist") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BUYER','SELLER')") @SecurityRequirement(name = "bearerAuth")
@Tag(name = "Lista de deseos", description = "Favoritos del comprador")
public class WishlistController {
    private final WishlistService wishlistService;
    @GetMapping @Operation(summary = "Ver mi lista de deseos")
    public ResponseEntity<ApiResponse<PageResponse<WishlistDTOs.WishlistItemResponse>>> getWishlist(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(user.getId(), PageRequest.of(page, size)))); }
    @PostMapping("/products/{productId}") @Operation(summary = "Agregar a lista de deseos")
    public ResponseEntity<ApiResponse<WishlistDTOs.WishlistItemResponse>> add(
            @PathVariable Long productId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Agregado a favoritos", wishlistService.addToWishlist(productId, user.getId()))); }
    @DeleteMapping("/products/{productId}") @Operation(summary = "Quitar de lista de deseos")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long productId, @AuthenticationPrincipal User user) {
        wishlistService.removeFromWishlist(productId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Eliminado de favoritos", null)); }
    @GetMapping("/products/{productId}/check") @Operation(summary = "¿Está en mi lista de deseos?")
    public ResponseEntity<ApiResponse<Boolean>> check(@PathVariable Long productId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.isInWishlist(productId, user.getId()))); }
}
