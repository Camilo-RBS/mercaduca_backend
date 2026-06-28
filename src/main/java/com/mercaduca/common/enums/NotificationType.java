package com.mercaduca.common.enums;

public enum NotificationType {
    // Comprador — flujo de compra
    PURCHASE_CONFIRMED,
    SHIPMENT_UPDATE,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED,
    ORDER_REFUNDED,

    // Vendedor — actividad de tienda
    NEW_ORDER,
    NEW_DISPUTE,
    DISPUTE_RESOLVED,
    DISPUTE_SELLER_RESPONDED,
    REVIEW_RECEIVED,

    // Vendedor — estado de cuenta
    SELLER_APPROVED,
    SELLER_REJECTED,
    SELLER_SUSPENDED,
    SELLER_BLOCKED,
    SELLER_WARNING,

    // Precio y catálogo
    PRICE_REDUCED,

    // Chat y preguntas
    NEW_MESSAGE,
    NEW_QUESTION,
    NEW_ANSWER,

    // Administración — cuenta de usuario
    ACCOUNT_DISABLED,
    ACCOUNT_ENABLED,
}
