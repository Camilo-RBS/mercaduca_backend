# Mercaduca — Backend API

Plataforma de marketplace multi-vendedor desarrollada con Spring Boot 3 y Java 21. Expone una API REST segura con JWT para operaciones de compra, venta, pagos, chat, notificaciones y administración.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Spring Security + JWT | JJWT 0.12 |
| Spring Data JPA / Hibernate | 6 |
| PostgreSQL | 15+ |
| SpringDoc OpenAPI (Swagger) | 2.5.0 |
| Lombok | 1.18 |
| Maven | 3.9+ |

---

## Requisitos previos

- Java 21 instalado (`java -version`)
- Maven 3.9+ (`mvn -version`)
- PostgreSQL 15+ corriendo localmente
- Base de datos `mercaduca` creada

```sql
CREATE DATABASE mercaduca;
```

---

## Configuración

Las variables de entorno tienen valores por defecto que funcionan en un entorno local estándar. Solo necesitas configurarlas si tu setup es diferente.

Crea un archivo `.env` o configura las variables directamente en tu sistema:

```env
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/mercaduca
DB_USERNAME=postgres
DB_PASSWORD=admin

# JWT (puedes dejar el valor por defecto en desarrollo)
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Servidor
SERVER_PORT=8080

# CORS — origen del frontend
CORS_ORIGINS=http://localhost:3000

# Directorio de uploads
UPLOAD_DIR=./uploads
```

> Las variables de pagos y envíos (Stripe, PayPal, DHL, etc.) tienen placeholders por defecto y no bloquean el arranque del sistema.

---

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/Camilo-RBS/mercaduca_backend.git
cd mercaduca_backend

# 2. Compilar el proyecto
mvn clean install -DskipTests

# 3. Ejecutar
mvn spring-boot:run
```

El servidor arranca en: `http://localhost:8080`

> Hibernate crea todas las tablas automáticamente al primer arranque (`ddl-auto=update`). No es necesario ejecutar scripts SQL manualmente en una base de datos nueva.

---

## Documentación interactiva de la API

Una vez corriendo el backend, accede a Swagger UI:

```
http://localhost:8080/api/v1/swagger-ui.html
```

Especificación OpenAPI (JSON):

```
http://localhost:8080/api/v1/api-docs
```

---

## Estructura del proyecto

```
src/main/java/com/mercaduca/
├── address/          # Direcciones de envío
├── auth/             # Autenticación (login, register, refresh token)
├── cart/             # Carrito de compras
├── categories/       # Categorías de productos
├── chat/             # Mensajería comprador–vendedor
├── common/           # DTOs, enums, utilidades compartidas
├── config/           # Configuración (CORS, Swagger, Security)
├── coupons/          # Cupones de descuento
├── disputes/         # Disputas y reclamaciones
├── exceptions/       # Manejo global de errores
├── notifications/    # Notificaciones en tiempo real
├── orders/           # Gestión de órdenes
├── products/         # Productos y preguntas
├── reports/          # Panel admin y reportes
├── reviews/          # Reseñas y calificaciones
├── security/         # Filtros JWT y configuración de seguridad
├── shipping/         # Cotizaciones y tracking de envíos
├── users/            # Usuarios, perfiles de vendedor
├── warnings/         # Advertencias administrativas
└── wishlist/         # Lista de deseos
```

---

## Roles del sistema

| Rol | Descripción |
|---|---|
| `BUYER` | Usuario comprador. Puede buscar, comprar, reseñar y chatear. |
| `SELLER` | Hereda permisos de BUYER. Puede publicar productos y gestionar ventas. |
| `ADMIN` | Acceso total. Gestión de usuarios, productos, órdenes y reportes. |

---

## Base de datos

El diagrama Entidad-Relación está disponible en el archivo `ER_Diagram.pdf` en la raíz del repositorio.

**Tablas principales:**
`users` · `seller_profiles` · `products` · `categories` · `orders` · `order_items` · `reviews` · `notifications` · `cart_items` · `wishlist_items` · `chat_messages` · `disputes` · `coupons` · `addresses` · `seller_warnings` · `product_questions` · `product_images`

---

## Scripts de migración

Necesarios **solo** si la base de datos ya existía antes de esta versión:

| Archivo | Qué corrige |
|---|---|
| `fix_reviews_order_nullable.sql` | Hace nullable la columna `order_id` en `reviews` |
| `fix_notifications_constraint.sql` | Actualiza el CHECK constraint de `notifications.type` |

---

## Equipo

Proyecto académico — Universidad. Todos los derechos reservados.
