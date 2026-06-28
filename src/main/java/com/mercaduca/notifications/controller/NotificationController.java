package com.mercaduca.notifications.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.enums.NotificationType;
import com.mercaduca.notifications.service.NotificationService;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "User notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Data
    public static class NotificationResponse {
        private Long id;
        private NotificationType type;
        private String title;
        private String message;
        private Long referenceId;
        private boolean read;
        private LocalDateTime createdAt;
    }

    @GetMapping
    @Operation(summary = "Get my notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        Page<NotificationResponse> result = notificationService
                .getUserNotifications(user.getId(), PageRequest.of(page, size))
                .map(n -> {
                    NotificationResponse dto = new NotificationResponse();
                    dto.setId(n.getId());
                    dto.setType(n.getType());
                    dto.setTitle(n.getTitle());
                    dto.setMessage(n.getMessage());
                    dto.setReferenceId(n.getReferenceId());
                    dto.setRead(n.isRead());
                    dto.setCreatedAt(n.getCreatedAt());
                    return dto;
                });
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.countUnread(user.getId())));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
