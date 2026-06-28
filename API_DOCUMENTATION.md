# Mercaduca — Documentación de la API

**Base URL:** `http://localhost:8080/api/v1`  
**Autenticación:** Bearer Token (JWT) — incluir en header: `Authorization: Bearer <token>`  
**Swagger UI interactivo:** `http://localhost:8080/api/v1/swagger-ui.html`

---

## Índice

1. [Autenticación](#1-autenticación)
2. [Usuarios](#2-usuarios)
3. [Vendedores](#3-vendedores)
4. [Productos](#4-productos)
5. [Categorías](#5-categorías)
6. [Carrito](#6-carrito)
7. [Órdenes](#7-órdenes)
8. [Reseñas](#8-reseñas)
9. [Lista de deseos](#9-lista-de-deseos)
10. [Direcciones](#10-direcciones)
11. [Chat](#11-chat)
12. [Notificaciones](#12-notificaciones)
13. [Cupones](#13-cupones)
14. [Disputas](#14-disputas)
15. [Envíos](#15-envíos)
16. [Administración](#16-administración)

---

## Formato de respuesta

Todas las respuestas siguen la misma estructura:

```json
{
  "success": true,
  "message": "OK",
  "data": { }
}
```

En caso de error:

```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null
}
```

---

## 1. Autenticación

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | ❌ | Registrar nuevo usuario |
| `POST` | `/auth/login` | ❌ | Iniciar sesión |
| `POST` | `/auth/refresh` | ❌ | Renovar access token |

### POST /auth/register
```json
{
  "username": "juanperez",
  "email": "juan@email.com",
  "password": "minimo8caracteres",
  "firstName": "Juan",
  "lastName": "Perez",
  "phoneNumber": "+503 7000-0000"
}
```

### POST /auth/login
```json
{
  "email": "juan@email.com",
  "password": "minimo8caracteres"
}
```
**Respuesta:** `{ accessToken, refreshToken, user: { id, username, email, role } }`

### POST /auth/refresh
```json
{
  "refreshToken": "eyJ..."
}
```

---

## 2. Usuarios

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/users/me` | ✅ | Todos | Ver mi perfil |
| `PUT` | `/users/me` | ✅ | Todos | Actualizar mi perfil |
| `PATCH` | `/users/me/password` | ✅ | Todos | Cambiar contraseña |
| `GET` | `/users/search?keyword=` | ✅ | Todos | Buscar usuarios |

### GET /users/me
**Respuesta:** perfil completo incluyendo `sellerProfile` si aplica.

### PUT /users/me
```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "phoneNumber": "+503 7000-0000",
  "profilePicture": "https://..."
}
```

### PATCH /users/me/password
```json
{
  "currentPassword": "actual",
  "newPassword": "nueva_min8"
}
```

---

## 3. Vendedores

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/sellers/register` | ✅ | BUYER/SELLER | Solicitar cuenta de vendedor |
| `GET` | `/seller/reports` | ✅ | SELLER | Dashboard de mi tienda |

### POST /sellers/register
```json
{
  "storeName": "Mi Tienda",
  "storeDescription": "Descripción de la tienda",
  "taxId": "0614-010101-101-0"
}
```

---

## 4. Productos

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/products` | ✅ | SELLER | Crear producto |
| `GET` | `/products/{id}` | ❌ | Público | Ver producto por ID |
| `GET` | `/products/search` | ❌ | Público | Buscar y filtrar productos |
| `PUT` | `/products/{id}` | ✅ | SELLER/ADMIN | Actualizar producto |
| `DELETE` | `/products/{id}` | ✅ | SELLER/ADMIN | Desactivar producto |
| `GET` | `/products/{id}/questions` | ❌ | Público | Preguntas del producto |
| `POST` | `/products/{id}/questions` | ✅ | BUYER | Hacer pregunta |
| `POST` | `/products/{productId}/questions/{questionId}/answer` | ✅ | SELLER | Responder pregunta |

### POST /products
```json
{
  "title": "Producto de ejemplo",
  "description": "Descripción detallada",
  "price": 29.99,
  "originalPrice": 39.99,
  "stock": 50,
  "categoryId": 1,
  "images": ["https://..."],
  "sku": "PROD-001",
  "weightKg": 0.5
}
```

### GET /products/search — Parámetros
| Parámetro | Tipo | Descripción |
|---|---|---|
| `keyword` | string | Búsqueda por nombre |
| `categoryId` | long | Filtrar por categoría |
| `minPrice` | decimal | Precio mínimo |
| `maxPrice` | decimal | Precio máximo |
| `sellerId` | long | Filtrar por vendedor |
| `sortBy` | string | `price_asc`, `price_desc`, `rating`, `newest` |
| `page` | int | Página (default: 0) |
| `size` | int | Tamaño (default: 12) |

---

## 5. Categorías

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/categories` | ❌ | Público | Todas las categorías activas |
| `GET` | `/categories/root` | ❌ | Público | Categorías raíz |
| `GET` | `/categories/{id}` | ❌ | Público | Categoría por ID |
| `GET` | `/categories/{id}/subcategories` | ❌ | Público | Subcategorías |
| `POST` | `/categories` | ✅ | ADMIN | Crear categoría |
| `PUT` | `/categories/{id}` | ✅ | ADMIN | Actualizar categoría |
| `DELETE` | `/categories/{id}` | ✅ | ADMIN | Desactivar categoría (soft delete) |

### POST /categories
```json
{
  "name": "Electrónica",
  "description": "Dispositivos electrónicos",
  "imageUrl": "https://...",
  "parentId": null
}
```

---

## 6. Carrito

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/cart` | ✅ | BUYER/SELLER | Ver carrito actual |
| `POST` | `/cart/items` | ✅ | BUYER/SELLER | Agregar ítem al carrito |
| `PUT` | `/cart/items/{itemId}` | ✅ | BUYER/SELLER | Actualizar cantidad |
| `DELETE` | `/cart/items/{itemId}` | ✅ | BUYER/SELLER | Eliminar ítem |
| `DELETE` | `/cart` | ✅ | BUYER/SELLER | Vaciar carrito |

### POST /cart/items
```json
{
  "productId": 1,
  "quantity": 2
}
```

---

## 7. Órdenes

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/orders` | ✅ | BUYER/SELLER | Crear orden desde carrito |
| `GET` | `/orders/my` | ✅ | BUYER/SELLER | Mis compras (paginado) |
| `GET` | `/orders/{id}` | ✅ | Todos | Ver orden por ID |
| `GET` | `/orders/number/{orderNumber}` | ✅ | Todos | Ver orden por número |
| `GET` | `/orders/seller` | ✅ | SELLER | Ventas recibidas |
| `PATCH` | `/orders/{id}/status` | ✅ | SELLER/ADMIN | Actualizar estado |
| `DELETE` | `/orders/{id}/cancel` | ✅ | BUYER | Cancelar orden |

### POST /orders
```json
{
  "shippingAddressId": 1,
  "paymentMethod": "CREDIT_CARD",
  "couponCode": "DESCUENTO10",
  "notes": "Dejar en portería"
}
```

### PATCH /orders/{id}/status
```json
{
  "status": "SHIPPED",
  "trackingNumber": "DHL123456789"
}
```

**Estados de orden:** `PENDING` → `PAID` → `SHIPPED` → `DELIVERED` | `CANCELLED`

---

## 8. Reseñas

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/reviews` | ✅ | BUYER/SELLER | Enviar reseña |
| `GET` | `/reviews/product/{productId}` | ❌ | Público | Reseñas de un producto |
| `POST` | `/reviews/{reviewId}/response` | ✅ | SELLER | Responder reseña |

### POST /reviews
```json
{
  "productId": 1,
  "orderId": 5,
  "rating": 5,
  "comment": "Excelente producto, muy recomendado."
}
```

---

## 9. Lista de deseos

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/wishlist` | ✅ | BUYER/SELLER | Ver lista de deseos |
| `POST` | `/wishlist/products/{productId}` | ✅ | BUYER/SELLER | Agregar producto |
| `DELETE` | `/wishlist/products/{productId}` | ✅ | BUYER/SELLER | Quitar producto |
| `GET` | `/wishlist/products/{productId}/check` | ✅ | BUYER/SELLER | ¿Está en favoritos? |

---

## 10. Direcciones

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/addresses` | ✅ | BUYER/SELLER | Ver mis direcciones |
| `POST` | `/addresses` | ✅ | BUYER/SELLER | Agregar dirección |
| `PUT` | `/addresses/{id}` | ✅ | BUYER/SELLER | Actualizar dirección |
| `DELETE` | `/addresses/{id}` | ✅ | BUYER/SELLER | Eliminar dirección |
| `PATCH` | `/addresses/{id}/default` | ✅ | BUYER/SELLER | Marcar como predeterminada |

### POST /addresses
```json
{
  "alias": "Casa",
  "fullName": "Juan Perez",
  "street": "Calle Principal 123",
  "city": "San Salvador",
  "state": "San Salvador",
  "country": "El Salvador",
  "zipCode": "01101",
  "phone": "+503 7000-0000"
}
```

---

## 11. Chat

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/chat/conversations` | ✅ | Todos | Mis conversaciones |
| `GET` | `/chat/conversations/{conversationId}/messages` | ✅ | Todos | Mensajes de una conversación |
| `POST` | `/chat/messages` | ✅ | Todos | Enviar mensaje |
| `PATCH` | `/chat/conversations/{conversationId}/read` | ✅ | Todos | Marcar como leído |
| `GET` | `/chat/orders/{orderId}/conversation-id` | ✅ | ADMIN | ID de conversación por orden |

### POST /chat/messages
```json
{
  "recipientId": 5,
  "content": "Hola, ¿tienes este producto disponible?",
  "productId": 12
}
```

> El `conversationId` se genera automáticamente como `conv_{min(id1,id2)}_{max(id1,id2)}`.

---

## 12. Notificaciones

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `GET` | `/notifications` | ✅ | Todos | Mis notificaciones (paginado) |
| `GET` | `/notifications/unread-count` | ✅ | Todos | Cantidad de no leídas |
| `PATCH` | `/notifications/read-all` | ✅ | Todos | Marcar todas como leídas |

**Tipos de notificación:**

| Tipo | Descripción |
|---|---|
| `PURCHASE_CONFIRMED` | Compra confirmada |
| `ORDER_SHIPPED` | Pedido enviado |
| `ORDER_DELIVERED` | Pedido entregado |
| `ORDER_CANCELLED` | Orden cancelada |
| `NEW_ORDER` | Nueva venta recibida (vendedor) |
| `SELLER_APPROVED` | Cuenta de vendedor aprobada |
| `SELLER_REJECTED` | Cuenta de vendedor rechazada |
| `SELLER_SUSPENDED` | Cuenta de vendedor suspendida |
| `SELLER_WARNING` | Advertencia administrativa |
| `ACCOUNT_DISABLED` | Cuenta desactivada por admin |
| `ACCOUNT_ENABLED` | Cuenta reactivada por admin |
| `NEW_DISPUTE` | Nueva disputa abierta |
| `DISPUTE_RESOLVED` | Disputa resuelta |
| `NEW_MESSAGE` | Nuevo mensaje de chat |
| `REVIEW_RECEIVED` | Nueva reseña recibida |
| `PRICE_REDUCED` | Bajó el precio de un favorito |

---

## 13. Cupones

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/coupons` | ✅ | SELLER | Crear cupón |
| `GET` | `/coupons/my` | ✅ | SELLER | Mis cupones |
| `POST` | `/coupons/validate` | ✅ | BUYER/SELLER | Validar código de cupón |
| `DELETE` | `/coupons/{id}` | ✅ | SELLER | Desactivar cupón |
| `DELETE` | `/coupons/{id}/permanent` | ✅ | SELLER | Eliminar cupón inactivo |

### POST /coupons
```json
{
  "code": "VERANO20",
  "description": "20% de descuento en verano",
  "discountType": "PERCENTAGE",
  "discountValue": 20,
  "minimumOrderAmount": 10.00,
  "maximumDiscount": 50.00,
  "startDate": "2026-06-01T00:00:00",
  "endDate": "2026-08-31T23:59:59",
  "usageLimit": 100
}
```

**Tipos de descuento:** `PERCENTAGE` | `FIXED_AMOUNT`

---

## 14. Disputas

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/disputes/orders/{orderId}` | ✅ | BUYER/SELLER | Abrir disputa |
| `GET` | `/disputes/my` | ✅ | BUYER/SELLER | Mis disputas como comprador |
| `GET` | `/disputes/seller` | ✅ | SELLER | Disputas sobre mis productos |
| `PATCH` | `/disputes/{id}/seller-response` | ✅ | SELLER | Responder disputa |
| `GET` | `/disputes/{id}` | ✅ | Todos | Ver disputa por ID |
| `GET` | `/disputes` | ✅ | ADMIN | Todas las disputas |
| `PATCH` | `/disputes/{id}/resolve` | ✅ | ADMIN | Resolver disputa |

### POST /disputes/orders/{orderId}
```json
{
  "reason": "Producto dañado",
  "description": "El producto llegó con la pantalla rota."
}
```

**Estados de disputa:** `OPEN` → `UNDER_REVIEW` → `RESOLVED_BUYER` | `RESOLVED_SELLER` | `CLOSED`

---

## 15. Envíos

| Método | Endpoint | Auth | Rol | Descripción |
|---|---|---|---|---|
| `POST` | `/shipping/quotes` | ✅ | BUYER/SELLER | Cotizar envío |
| `GET` | `/shipping/track/{provider}/{trackingNumber}` | ✅ | Todos | Rastrear envío |

### POST /shipping/quotes
```json
{
  "originCity": "San Salvador",
  "destinationCity": "Santa Ana",
  "weightKg": 1.5,
  "orderId": 10
}
```

---

## 16. Administración

> Todos los endpoints de administración requieren rol `ADMIN`.

### Órdenes

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/admin/orders` | Todas las órdenes (paginado) |
| `GET` | `/admin/orders/status/{status}` | Órdenes por estado |
| `GET` | `/admin/orders/search?keyword=` | Buscar por número de orden o comprador |

### Reportes

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/admin/reports/dashboard` | Dashboard general de la plataforma |
| `GET` | `/admin/reports/sellers/{sellerId}` | Reporte detallado de un vendedor |

### Gestión de usuarios

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/admin/users` | Todos los usuarios |
| `GET` | `/admin/users/{userId}` | Ver perfil de un usuario |
| `PATCH` | `/admin/users/{userId}/toggle-status` | Activar o desactivar cuenta |

### Gestión de vendedores

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/admin/sellers` | Todos los vendedores con estadísticas |
| `GET` | `/admin/sellers/pending` | Solicitudes pendientes |
| `PATCH` | `/admin/sellers/{sellerId}/approve` | Aprobar vendedor |
| `PATCH` | `/admin/sellers/{sellerId}/reject` | Rechazar vendedor |
| `PATCH` | `/admin/sellers/{sellerId}/suspend` | Suspender vendedor |
| `PATCH` | `/admin/sellers/{sellerId}/block` | Bloquear vendedor permanentemente |
| `PATCH` | `/admin/sellers/{sellerId}/unblock` | Desbloquear vendedor |
| `POST` | `/admin/users/{sellerId}/warn` | Enviar advertencia |
| `GET` | `/admin/users/{sellerId}/warnings` | Ver historial de advertencias |

### PATCH /admin/sellers/{sellerId}/reject
```json
{
  "reason": "Documentación incompleta o inválida."
}
```

### POST /admin/users/{sellerId}/warn
```json
{
  "reason": "Descripción de precios engañosa en productos."
}
```

---

## Códigos de respuesta HTTP

| Código | Significado |
|---|---|
| `200` | OK |
| `201` | Recurso creado |
| `400` | Datos inválidos o regla de negocio incumplida |
| `401` | No autenticado o token inválido/expirado |
| `403` | Sin permisos para esta acción |
| `404` | Recurso no encontrado |
| `500` | Error interno del servidor |

---

## Autenticación paso a paso

1. Llamar a `POST /auth/login` con email y contraseña.
2. Guardar `accessToken` y `refreshToken` de la respuesta.
3. Incluir en todas las peticiones protegidas: `Authorization: Bearer <accessToken>`.
4. Cuando el `accessToken` expire (24h), renovarlo con `POST /auth/refresh` usando el `refreshToken` (válido 7 días).
5. Si ambos tokens expiran, el usuario debe hacer login nuevamente.
