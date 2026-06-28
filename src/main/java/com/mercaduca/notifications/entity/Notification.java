package com.mercaduca.notifications.entity;

import com.mercaduca.common.enums.NotificationType;
import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_read", columnList = "is_read")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    // columnDefinition explícito: evita que Hibernate 6 genere un CHECK constraint
    // automático con la lista de valores del enum. Sin esto, cada vez que se añade
    // un valor al enum hay que actualizar manualmente el constraint en PostgreSQL.
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "reference_id")
    private Long referenceId; // orderId, productId, etc.

    @Column(name = "is_read")
    @Builder.Default
    private boolean read = false;
}
