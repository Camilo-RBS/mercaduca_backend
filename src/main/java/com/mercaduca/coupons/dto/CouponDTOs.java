package com.mercaduca.coupons.dto;

import com.mercaduca.coupons.entity.Coupon;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDTOs {

    @Data
    public static class CreateCouponRequest {
        @NotBlank @Size(max = 50)
        private String code;

        private String description;

        @NotNull
        private Coupon.DiscountType discountType;

        @NotNull @DecimalMin("0.01")
        private BigDecimal discountValue;

        private BigDecimal minimumOrderAmount;
        private BigDecimal maximumDiscount;
        private Long categoryId;

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        private Integer usageLimit;
    }

    @Data
    public static class ValidateCouponRequest {
        @NotBlank
        private String code;
        private BigDecimal orderAmount;
    }

    @Data
    public static class CouponResponse {
        private Long id;
        private String code;
        private String description;
        private Coupon.DiscountType discountType;
        private BigDecimal discountValue;
        private BigDecimal minimumOrderAmount;
        private BigDecimal maximumDiscount;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer usageLimit;
        private Integer usageCount;
        private boolean active;
        private boolean valid;
    }
}
