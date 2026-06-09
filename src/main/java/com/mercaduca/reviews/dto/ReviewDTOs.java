package com.mercaduca.reviews.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class ReviewDTOs {

    @Data
    public static class CreateReviewRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        // Opcional: si se provee, se usará para validar compra específica;
        // si no, se busca cualquier orden del comprador con ese producto.
        private Long orderId;

        @NotNull
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        private Integer rating;

        @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
        private String comment;
    }

    @Data
    public static class SellerResponseRequest {
        @NotBlank(message = "Response cannot be blank")
        @Size(max = 500)
        private String response;
    }

    @Data
    public static class ReviewResponse {
        private Long id;
        private Long productId;
        private String productTitle;
        private Long buyerId;
        private String buyerName;
        private Integer rating;
        private String comment;
        private String sellerResponse;
        private boolean verifiedPurchase;
        private LocalDateTime createdAt;
    }
}
