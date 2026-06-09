-- ============================================================
-- MIGRACIÓN COMPLETA: Arreglar TODOS los CHECK constraints
-- de columnas enum en PostgreSQL
-- ============================================================
-- Ejecutar este script en pgAdmin conectado a la BD "mercaduca"
-- Después reiniciar el backend una sola vez
-- ============================================================

-- ── notifications ─────────────────────────────────────────
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check;
ALTER TABLE notifications ADD CONSTRAINT notifications_type_check
CHECK (type IN (
    'PURCHASE_CONFIRMED','SHIPMENT_UPDATE','ORDER_SHIPPED','ORDER_DELIVERED',
    'ORDER_CANCELLED','ORDER_REFUNDED','NEW_ORDER','NEW_DISPUTE',
    'DISPUTE_RESOLVED','DISPUTE_SELLER_RESPONDED','REVIEW_RECEIVED',
    'SELLER_APPROVED','SELLER_REJECTED','SELLER_SUSPENDED','SELLER_BLOCKED',
    'SELLER_WARNING','PRICE_REDUCED','NEW_MESSAGE','NEW_QUESTION','NEW_ANSWER'
));

-- ── disputes ──────────────────────────────────────────────
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS disputes_status_check;
ALTER TABLE disputes ADD CONSTRAINT disputes_status_check
CHECK (status IN ('OPEN','UNDER_REVIEW','RESOLVED_BUYER','RESOLVED_SELLER','CLOSED','ARCHIVED'));

-- ── orders ────────────────────────────────────────────────
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check;
ALTER TABLE orders ADD CONSTRAINT orders_status_check
CHECK (status IN ('PENDING','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED','REFUNDED'));

ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_payment_method_check;
ALTER TABLE orders ADD CONSTRAINT orders_payment_method_check
CHECK (payment_method IS NULL OR payment_method IN ('STRIPE','PAYPAL','BANK_TRANSFER'));

ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_shipping_provider_check;
ALTER TABLE orders ADD CONSTRAINT orders_shipping_provider_check
CHECK (shipping_provider IS NULL OR shipping_provider IN ('DHL','CORREOS','UBER_DIRECT'));

-- ── products ──────────────────────────────────────────────
ALTER TABLE products DROP CONSTRAINT IF EXISTS products_status_check;
ALTER TABLE products ADD CONSTRAINT products_status_check
CHECK (status IN ('ACTIVE','INACTIVE','DRAFT','DELETED'));

-- ── seller_profiles ───────────────────────────────────────
ALTER TABLE seller_profiles DROP CONSTRAINT IF EXISTS seller_profiles_status_check;
ALTER TABLE seller_profiles ADD CONSTRAINT seller_profiles_status_check
CHECK (status IN ('PENDING','VERIFIED','REJECTED','SUSPENDED','BLOCKED'));

-- ── users ─────────────────────────────────────────────────
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check
CHECK (role IN ('ADMIN','SELLER','BUYER'));

-- ── coupons ───────────────────────────────────────────────
ALTER TABLE coupons DROP CONSTRAINT IF EXISTS coupons_discount_type_check;
ALTER TABLE coupons ADD CONSTRAINT coupons_discount_type_check
CHECK (discount_type IN ('PERCENTAGE','FIXED_AMOUNT'));

-- Verificar que todos quedaron bien
SELECT conrelid::regclass AS tabla, conname AS constraint
FROM pg_constraint
WHERE contype = 'c'
  AND conrelid::regclass::text IN (
    'notifications','disputes','orders','products',
    'seller_profiles','users','coupons'
  )
ORDER BY tabla, constraint;
