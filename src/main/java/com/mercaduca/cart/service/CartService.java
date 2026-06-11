package com.mercaduca.cart.service;

import com.mercaduca.cart.dto.CartDTOs;

public interface CartService {
    CartDTOs.CartResponse getCart(Long userId);
    CartDTOs.CartResponse addToCart(CartDTOs.AddToCartRequest request, Long userId);
    CartDTOs.CartResponse updateCartItem(Long itemId, CartDTOs.UpdateCartItemRequest request, Long userId);
    CartDTOs.CartResponse removeFromCart(Long itemId, Long userId);
    void clearCart(Long userId);
}
