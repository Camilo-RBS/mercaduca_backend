package com.mercaduca.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class ChatDTOs {

    @Data
    public static class SendMessageRequest {
        @NotNull(message = "Recipient ID is required")
        private Long recipientId;

        @NotBlank(message = "Message content is required")
        private String content;

        private Long productId; // Opcional: contexto de producto
        private Long orderId;   // Opcional: contexto de orden (admin, disputas)
    }

    @Data
    public static class MessageResponse {
        private Long id;
        private String conversationId;
        private Long senderId;
        private String senderName;
        private Long recipientId;
        private String recipientName;
        private String content;
        private boolean read;
        private Long productId;
        private Long orderId;
        private LocalDateTime createdAt;
    }

    @Data
    public static class ConversationSummary {
        private String conversationId;
        private Long otherUserId;
        private String otherUserName;
        private String lastMessage;
        private long unreadCount;
        private LocalDateTime lastMessageAt;
    }
}
