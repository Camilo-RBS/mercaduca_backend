-- ============================================================
-- MIGRACIÓN: Arreglar CHECK constraint de notifications.type
-- ============================================================
-- El problema: Hibernate 6 creó un CHECK constraint con los
-- valores del enum NotificationType que existían al inicio.
-- Cuando se añadieron NEW_ORDER, DISPUTE_SELLER_RESPONDED, etc.,
-- ddl-auto=update NO actualizó el constraint, causando que las
-- notificaciones de nuevas órdenes follen y reviertan las compras.
--
-- INSTRUCCIONES:
-- 1. Conectarse a la base de datos mercaduca en PostgreSQL
-- 2. Ejecutar este script completo
-- 3. Reiniciar el backend
-- ============================================================

-- Paso 1: Eliminar el constraint antiguo
ALTER TABLE notifications
    DROP CONSTRAINT IF EXISTS notifications_type_check;

-- Paso 2: Recrear con TODOS los valores actuales del enum NotificationType
-- (incluyendo los que fueron añadidos después de la creación inicial)
ALTER TABLE notifications
    ADD CONSTRAINT notifications_type_check
    CHECK (type IN (
        -- Comprador — flujo de compra
        'PURCHASE_CONFIRMED',
        'SHIPMENT_UPDATE',
        'ORDER_SHIPPED',
        'ORDER_DELIVERED',
        'ORDER_CANCELLED',
        'ORDER_REFUNDED',
        -- Vendedor — actividad de tienda
        'NEW_ORDER',
        'NEW_DISPUTE',
        'DISPUTE_RESOLVED',
        'DISPUTE_SELLER_RESPONDED',
        'REVIEW_RECEIVED',
        -- Vendedor — estado de cuenta
        'SELLER_APPROVED',
        'SELLER_REJECTED',
        'SELLER_SUSPENDED',
        'SELLER_BLOCKED',
        'SELLER_WARNING',
        -- Precio y catálogo
        'PRICE_REDUCED',
        -- Chat y preguntas
        'NEW_MESSAGE',
        'NEW_QUESTION',
        'NEW_ANSWER'
    ));

-- Verificar que el constraint fue creado correctamente
SELECT conname, consrc
FROM pg_constraint
WHERE conrelid = 'notifications'::regclass
  AND contype = 'c';
