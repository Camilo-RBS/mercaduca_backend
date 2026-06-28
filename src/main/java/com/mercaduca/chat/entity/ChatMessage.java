package com.mercaduca.chat.entity;

import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_conversation", columnList = "conversation_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_read")
    @Builder.Default
    private boolean read = false;

    @Column(name = "product_id")
    private Long productId;

    /** Referencia opcional a una orden — permite filtrar por contexto de orden */
    @Column(name = "order_id")
    private Long orderId;
}
