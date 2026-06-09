package com.mercaduca.wishlist.service;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.wishlist.dto.WishlistDTOs;
import org.springframework.data.domain.Pageable;
public interface WishlistService {
    WishlistDTOs.WishlistItemResponse addToWishlist(Long productId, Long userId);
    void removeFromWishlist(Long productId, Long userId);
    PageResponse<WishlistDTOs.WishlistItemResponse> getWishlist(Long userId, Pageable pageable);
    boolean isInWishlist(Long productId, Long userId);
}
