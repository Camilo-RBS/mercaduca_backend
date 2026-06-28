-- ─────────────────────────────────────────────────────────────────────
-- Fix: allow NULL in reviews.order_id
-- El entity Review.java define order como nullable = true pero la tabla
-- fue creada con NOT NULL. Hibernate ddl-auto=update no elimina esta
-- constraint automáticamente.
-- ─────────────────────────────────────────────────────────────────────

ALTER TABLE reviews ALTER COLUMN order_id DROP NOT NULL;
