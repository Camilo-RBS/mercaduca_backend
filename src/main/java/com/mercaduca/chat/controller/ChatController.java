package com.mercaduca.chat.controller;

import com.mercaduca.chat.dto.ChatDTOs;
import com.mercaduca.chat.service.ChatService;
import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat", description = "Buyer-seller messaging")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/messages")
    @Operation(summary = "Send a message via REST")
    public ResponseEntity<ApiResponse<ChatDTOs.MessageResponse>> sendMessage(
            @Valid @RequestBody ChatDTOs.SendMessageRequest request,
            @AuthenticationPrincipal User user) {
        ChatDTOs.MessageResponse response = chatService.sendMessage(request, user.getId());
        messagingTemplate.convertAndSendToUser(
                response.getRecipientId().toString(),
                "/queue/messages",
                response
        );
        return ResponseEntity.ok(ApiResponse.success("Message sent", response));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get messages of a conversation")
    public ResponseEntity<ApiResponse<PageResponse<ChatDTOs.MessageResponse>>> getConversation(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getConversation(conversationId, PageRequest.of(page, size))));
    }

    /**
     * Retorna ConversationSummary en lugar de String para incluir
     * el nombre del otro participante directamente en la bandeja.
     */
    @GetMapping("/conversations")
    @Operation(summary = "Get my conversations with participant names")
    public ResponseEntity<ApiResponse<List<ChatDTOs.ConversationSummary>>> getMyConversations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.success(chatService.getMyConversations(user.getId())));
    }

    @PatchMapping("/conversations/{conversationId}/read")
    @Operation(summary = "Mark conversation as read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable String conversationId,
            @AuthenticationPrincipal User user) {
        chatService.markConversationRead(conversationId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Conversation marked as read", null));
    }

    /**
     * Devuelve el ID de conversación entre el admin (u) y otro usuario
     * en el contexto de una orden específica.
     * Permite al admin abrir/encontrar el hilo correcto sin saber el conversationId.
     */
    @GetMapping("/orders/{orderId}/conversation-id")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener conversationId para admin–usuario en contexto de orden")
    public ResponseEntity<ApiResponse<String>> getOrderConversationId(
            @PathVariable Long orderId,
            @RequestParam Long withUserId,
            @AuthenticationPrincipal User admin) {
        String convId = chatService.buildConversationId(admin.getId(), withUserId);
        return ResponseEntity.ok(ApiResponse.success(convId));
    }

    @MessageMapping("/chat.send")
    public void handleWebSocketMessage(@Payload ChatDTOs.SendMessageRequest request,
                                        @AuthenticationPrincipal User user) {
        ChatDTOs.MessageResponse response = chatService.sendMessage(request, user.getId());
        messagingTemplate.convertAndSendToUser(
                response.getRecipientId().toString(), "/queue/messages", response);
    }
}
