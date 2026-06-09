package com.mercaduca.wishlist.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class WishlistDTOs {
    @Data public static class WishlistItemResponse {
        private Long id; private Long productId; private String productTitle;
        private String productImage; private BigDecimal price; private String status;
        private Double averageRating; private LocalDateTime addedAt;
    }
}
