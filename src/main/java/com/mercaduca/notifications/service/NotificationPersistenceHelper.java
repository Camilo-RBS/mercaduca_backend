package com.mercaduca.notifications.service;

import com.mercaduca.common.enums.NotificationType;
import com.mercaduca.notifications.entity.Notification;
import com.mercaduca.notifications.repository.NotificationRepository;
import com.mercaduca.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Componente separado de NotificationService para persistir notificaciones.
 *
 * POR QUÉ EXISTE ESTA CLASE:
 * Spring AOP no intercepta llamadas a métodos dentro de la misma clase (self-invocation).
 * Si NotificationService llamara a su propio save() con @Transactional(REQUIRES_NEW),
 * la anotación sería ignorada y la notificación se ejecutaría en la misma transacción
 * del llamador (ej: la orden de compra).
 *
 * Al separar la persistencia en este componente:
 * 1. NotificationService.notifyXxx() → llama helper.save() → pasa por el proxy de Spring
 * 2. Spring crea una transacción NUEVA e independiente para cada notificación
 * 3. Si la notificación falla (ej: CHECK constraint desactualizado en PostgreSQL),
 *    SOLO esa transacción se revierte — la orden de compra ya está committed
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPersistenceHelper {

    private final NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(User user, NotificationType type, String title, String message, Long refId) {
        try {
            Notification n = Notification.builder()
                    .user(user)
                    .type(type)
                    .title(title)
                    .message(message)
                    .referenceId(refId)
                    .build();
            notificationRepository.save(n);
            log.info("Notification [{}] → user {}", type, user.getId());
        } catch (Exception e) {
            // No propagar. Un fallo en notificaciones NUNCA debe cancelar la operación
            // principal (compra, aprobación, etc.).
            // Causa más común: CHECK constraint de PostgreSQL desactualizado.
            // Solución definitiva: ejecutar fix_notifications_constraint.sql
            log.warn("Notification [{}] → user {} FAILED (non-critical): {}",
                    type, user.getId(), e.getMessage());
        }
    }
}
