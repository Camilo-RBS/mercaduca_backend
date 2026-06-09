package com.mercaduca.products.dto;

import com.mercaduca.common.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDTOs {

    @Data
    public static class CreateProductRequest {
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private BigDecimal price;

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        private Integer stock;

        @NotNull(message = "Category is required")
        private Long categoryId;

        private List<String> images;
        private String sku;
        private Double weightKg;
    }

    @Data
    public static class UpdateProductRequest {
        @Size(max = 200)
        private String title;
        private String description;

        @DecimalMin(value = "0.01")
        private BigDecimal price;

        @Min(0)
        private Integer stock;

        private Long categoryId;
        private List<String> images;
        private ProductStatus status;
        private Boolean featured;
        private Double weightKg;
    }

    @Data
    public static class ProductResponse {
        private Long id;
        private String title;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer stock;
        private List<String> images;
        private Long categoryId;
        private String categoryName;
        private Long sellerId;
        private String sellerName;
        private String sellerStoreName;
        private ProductStatus status;
        private boolean featured;
        private Double averageRating;
        private Integer totalReviews;
        private Integer totalSold;
        private Long viewCount;
        private String sku;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class ProductFilterRequest {
        private String keyword;
        private Long categoryId;
        private Long sellerId;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Boolean featured;
        private String sortBy;      // price, createdAt, averageRating, totalSold
        private String sortDir;     // asc, desc
    }
}
