package com.mercaduca.warnings.entity;

import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_warnings", indexes = {
        @Index(name = "idx_warning_seller", columnList = "seller_id")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SellerWarning extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "is_acknowledged")
    @Builder.Default
    private boolean acknowledged = false;
}
