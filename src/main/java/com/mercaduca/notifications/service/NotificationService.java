package com.mercaduca.notifications.service;

import com.mercaduca.common.enums.NotificationType;
import com.mercaduca.notifications.entity.Notification;
import com.mercaduca.notifications.repository.NotificationRepository;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Helper en componente SEPARADO para que @Transactional(REQUIRES_NEW) funcione.
     * Spring AOP no intercepta llamadas internas (self-invocation), por eso este
     * helper existe como un bean independiente — cada save() corre en su propia
     * transacción y un fallo no afecta la transacción del llamador.
     */
    private final NotificationPersistenceHelper helper;

    // ── Comprador — flujo de compra ───────────────────────────────────────────

    public void notifyPurchaseConfirmed(User buyer, Order order) {
        helper.save(buyer, NotificationType.PURCHASE_CONFIRMED,
                "✅ Compra confirmada",
                "Tu orden #" + order.getOrderNumber() + " fue confirmada. Total: $" + order.getTotal(),
                order.getId());
    }

    public void notifyShipmentUpdate(User buyer, Order order) {
        helper.save(buyer, NotificationType.ORDER_SHIPPED,
                "📦 Tu pedido está en camino",
                "Orden #" + order.getOrderNumber() + " fue enviada. Tracking: " + order.getTrackingNumber(),
                order.getId());
    }

    public void notifyOrderDelivered(User buyer, Order order) {
        helper.save(buyer, NotificationType.ORDER_DELIVERED,
                "🎉 Pedido entregado",
                "Tu orden #" + order.getOrderNumber() + " fue marcada como entregada.",
                order.getId());
    }

    public void notifyOrderCancelled(User buyer, Order order) {
        helper.save(buyer, NotificationType.ORDER_CANCELLED,
                "❌ Orden cancelada",
                "Tu orden #" + order.getOrderNumber() + " fue cancelada.",
                order.getId());
    }

    // ── Vendedor — actividad de tienda ────────────────────────────────────────

    public void notifySellerNewOrder(User seller, Order order) {
        helper.save(seller, NotificationType.NEW_ORDER,
                "🛍️ Nueva orden recibida",
                "Recibiste una nueva orden #" + order.getOrderNumber() + " por $" + order.getTotal(),
                order.getId());
    }

    public void notifySellerNewDispute(User seller, Long disputeId, String orderNumber) {
        helper.save(seller, NotificationType.NEW_DISPUTE,
                "⚠️ Nueva disputa abierta",
                "Se abrió una disputa para la orden #" + orderNumber + ". Responde pronto.",
                disputeId);
    }

    public void notifyDisputeResolved(User user, Long disputeId, String resolution) {
        helper.save(user, NotificationType.DISPUTE_RESOLVED,
                "✅ Disputa resuelta",
                "La disputa fue resuelta: " + resolution,
                disputeId);
    }

    public void notifySellerDisputeResponded(User buyer, Long disputeId) {
        helper.save(buyer, NotificationType.DISPUTE_SELLER_RESPONDED,
                "💬 El vendedor respondió tu disputa",
                "El vendedor envió una respuesta a tu disputa.",
                disputeId);
    }

    // ── Vendedor — estado de cuenta ───────────────────────────────────────────

    public void notifySellerApproved(User seller) {
        helper.save(seller, NotificationType.SELLER_APPROVED,
                "🎉 ¡Cuenta de vendedor aprobada!",
                "Tu solicitud fue aprobada. Ya puedes publicar productos en Mercaduca.",
                null);
    }

    public void notifySellerRejected(User seller, String reason) {
        helper.save(seller, NotificationType.SELLER_REJECTED,
                "❌ Solicitud de vendedor rechazada",
                "Tu solicitud fue rechazada. Motivo: " + reason,
                null);
    }

    public void notifySellerSuspended(User seller, String reason) {
        helper.save(seller, NotificationType.SELLER_SUSPENDED,
                "⚠️ Cuenta de vendedor suspendida",
                "Tu cuenta fue suspendida temporalmente. Motivo: " + reason +
                ". Puedes seguir comprando pero no publicar productos.",
                null);
    }

    public void notifySellerBlocked(User seller, String reason) {
        helper.save(seller, NotificationType.SELLER_BLOCKED,
                "🚫 Cuenta bloqueada",
                "Tu cuenta fue bloqueada permanentemente. Motivo: " + reason +
                ". Contacta a soporte si crees que es un error.",
                null);
    }

    public void notifySellerWarning(User seller, String reason) {
        helper.save(seller, NotificationType.SELLER_WARNING,
                "⚠️ Advertencia administrativa",
                "Has recibido una advertencia del equipo de Mercaduca. Motivo: " + reason,
                null);
    }

    // ── Administración — cuenta de usuario ───────────────────────────────────

    public void notifyAccountDisabled(User user) {
        helper.save(user, NotificationType.ACCOUNT_DISABLED,
                "🚫 Tu cuenta ha sido desactivada",
                "Un administrador ha desactivado tu cuenta indefinidamente. " +
                "Si crees que esto es un error, contacta al equipo de soporte.",
                null);
    }

    public void notifyAccountEnabled(User user) {
        helper.save(user, NotificationType.ACCOUNT_ENABLED,
                "✅ Tu cuenta ha sido reactivada",
                "Tu cuenta ha sido reactivada por un administrador. Ya puedes acceder normalmente.",
                null);
    }

    // ── Precio ────────────────────────────────────────────────────────────────

    public void notifyPriceReduced(User user, Long productId, String productTitle) {
        helper.save(user, NotificationType.PRICE_REDUCED,
                "🏷️ Bajó el precio",
                productTitle + " ahora tiene un precio más bajo. ¡Aprovecha!",
                productId);
    }

    // ── Consultas del sistema ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
