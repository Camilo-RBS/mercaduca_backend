package com.mercaduca.coupons.entity;

import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private DiscountType discountType; // PERCENTAGE or FIXED_AMOUNT

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "minimum_order_amount", precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(name = "maximum_discount", precision = 10, scale = 2)
    private BigDecimal maximumDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller; // null = platform-wide

    @Column(name = "category_id")
    private Long categoryId; // null = all categories

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active
                && now.isAfter(startDate)
                && now.isBefore(endDate)
                && (usageLimit == null || usageCount < usageLimit);
    }

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
}
