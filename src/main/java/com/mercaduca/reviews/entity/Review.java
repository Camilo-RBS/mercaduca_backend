package com.mercaduca.reviews.entity;

import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.products.entity.Product;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"buyer_id", "product_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "seller_response", columnDefinition = "TEXT")
    private String sellerResponse;

    @Column(name = "is_verified_purchase")
    @Builder.Default
    private boolean verifiedPurchase = true;
}
