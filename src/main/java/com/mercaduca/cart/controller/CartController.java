package com.mercaduca.cart.controller;

import com.mercaduca.cart.dto.CartDTOs;
import com.mercaduca.cart.service.CartService;
import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BUYER','SELLER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<ApiResponse<CartDTOs.CartResponse>> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getId())));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartDTOs.CartResponse>> addItem(
            @Valid @RequestBody CartDTOs.AddToCartRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Item added to cart",
                cartService.addToCart(request, user.getId())));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<ApiResponse<CartDTOs.CartResponse>> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartDTOs.UpdateCartItemRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Cart updated",
                cartService.updateCartItem(itemId, request, user.getId())));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartDTOs.CartResponse>> removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Item removed",
                cartService.removeFromCart(itemId, user.getId())));
    }

    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
