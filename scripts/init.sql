-- ─────────────────────────────────────────────────────────────────────
-- MERCADUCA — Datos iniciales para PostgreSQL
-- ─────────────────────────────────────────────────────────────────────

-- Categorías raíz
INSERT INTO categories (id, name, description, is_active, created_at, updated_at) VALUES
  (1,  'Electrónica',     'Teléfonos, laptops, gadgets',    true, NOW(), NOW()),
  (2,  'Ropa',            'Moda y accesorios',              true, NOW(), NOW()),
  (3,  'Hogar y Jardín',  'Muebles, herramientas, decor',   true, NOW(), NOW()),
  (4,  'Deportes',        'Equipos y ropa deportiva',       true, NOW(), NOW()),
  (5,  'Libros',          'Físicos y digitales',            true, NOW(), NOW()),
  (6,  'Juguetes',        'Juegos y juguetes',              true, NOW(), NOW()),
  (7,  'Belleza',         'Skincare y maquillaje',          true, NOW(), NOW()),
  (8,  'Automotriz',      'Piezas y accesorios',            true, NOW(), NOW()),
  (9,  'Alimentos',       'Despensa y bebidas',             true, NOW(), NOW()),
  (10, 'Salud',           'Suplementos y dispositivos',     true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Subcategorías
INSERT INTO categories (id, name, description, parent_id, is_active, created_at, updated_at) VALUES
  (11, 'Smartphones',     'Teléfonos móviles',    1, true, NOW(), NOW()),
  (12, 'Laptops',         'Computadoras portátiles', 1, true, NOW(), NOW()),
  (13, 'Ropa de Hombre',  'Ropa masculina',       2, true, NOW(), NOW()),
  (14, 'Ropa de Mujer',   'Ropa femenina',        2, true, NOW(), NOW()),
  (15, 'Zapatos',         'Calzado todo tipo',    2, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Admin (contraseña: Admin@12345 — BCrypt)
INSERT INTO users (id, username, email, password, first_name, last_name, role, is_enabled, is_account_non_locked, created_at, updated_at) VALUES
  (1, 'admin', 'admin@mercaduca.com',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Admin', 'Mercaduca', 'ADMIN', true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Secuencias
SELECT setval('categories_id_seq', 20, true);
SELECT setval('users_id_seq', 2, true);
