package com.mercaduca.orders.service;

import com.mercaduca.cart.entity.CartItem;
import com.mercaduca.cart.repository.CartRepository;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.common.enums.OrderStatus;
import com.mercaduca.coupons.entity.Coupon;
import com.mercaduca.coupons.repository.CouponRepository;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.notifications.service.NotificationService;
import com.mercaduca.orders.dto.OrderDTOs;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.orders.entity.OrderItem;
import com.mercaduca.orders.mapper.OrderMapper;
import com.mercaduca.orders.repository.OrderRepository;
import com.mercaduca.payments.service.PaymentService;
import com.mercaduca.payments.strategy.PaymentStrategy;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.shipping.service.ShippingService;
import com.mercaduca.shipping.strategy.ShippingStrategy;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final NotificationService notificationService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTOs.OrderResponse createOrder(OrderDTOs.CreateOrderRequest request, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", buyerId));

        List<CartItem> cartItems = cartRepository.findByUserId(buyerId);
        if (cartItems.isEmpty()) throw new BusinessException("Tu carrito está vacío");
        cartItems.forEach(ci -> {
            if (ci.getProduct().getStock() < ci.getQuantity())
                throw new BusinessException("Stock insuficiente para: " + ci.getProduct().getTitle());
        });

        List<OrderItem> orderItems = buildOrderItems(cartItems);
        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validar cupón ANTES del pago; incrementar usage DESPUÉS del pago exitoso
        BigDecimal discount = BigDecimal.ZERO;
        String couponCode = null;
        Coupon appliedCoupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            appliedCoupon = couponRepository.findByCode(request.getCouponCode().toUpperCase())
                    .orElseThrow(() -> new BusinessException("Código de cupón inválido"));
            if (!appliedCoupon.isValid()) {
                throw new BusinessException("El cupón está expirado o no es válido");
            }
            discount = calculateDiscount(appliedCoupon, subtotal);
            couponCode = appliedCoupon.getCode();
        }

        BigDecimal shippingCost = calculateShippingCost(request);

        BigDecimal total = subtotal.subtract(discount).add(shippingCost);

        PaymentStrategy.PaymentResult paymentResult = paymentService.processPayment(
                request.getPaymentMethod(),
                PaymentStrategy.PaymentRequest.builder()
                        .orderNumber("TEMP")
                        .amount(total)
                        .currency("USD")
                        .description("Orden Mercaduca")
                        .customerEmail(buyer.getEmail())
                        .paymentToken(request.getPaymentToken())
                        .build());

        if (!paymentResult.isSuccess()) {
            throw new BusinessException("Pago fallido: " + paymentResult.getMessage());
        }

        // Incrementar uso del cupón solo tras pago exitoso
        if (appliedCoupon != null) {
            appliedCoupon.setUsageCount(appliedCoupon.getUsageCount() + 1);
            couponRepository.save(appliedCoupon);
        }

        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .buyer(buyer)
                .status(OrderStatus.PAID)
                .subtotal(subtotal)
                .discountAmount(discount)
                .shippingCost(shippingCost)
                .total(total)
                .paymentMethod(request.getPaymentMethod())
                .paymentId(paymentResult.getPaymentId())
                .shippingProvider(request.getShippingProvider())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingCountry(request.getShippingCountry())
                .shippingZip(request.getShippingZip())
                .couponCode(couponCode)
                .notes(request.getNotes())
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);
        orderRepository.save(order);

        cartItems.forEach(ci -> {
            Product p = ci.getProduct();
            p.setStock(p.getStock() - ci.getQuantity());
            p.setTotalSold(p.getTotalSold() + ci.getQuantity());
            productRepository.save(p);
        });

        // Mapear la respuesta ANTES de borrar el carrito.
        // clearAutomatically=true en deleteAllByUserId limpia el persistence context,
        // lo que desatacharía las entidades lazy (buyer, items). Al mapear primero
        // garantizamos que todas las relaciones ya están cargadas en memoria.
        OrderDTOs.OrderResponse response = orderMapper.toResponse(order);

        // Colectar seller IDs antes del clear (los OrderItem ya están en memoria)
        List<Long> sellerIds = order.getItems().stream()
                .map(OrderItem::getSellerId)
                .distinct()
                .toList();

        // Vaciar carrito (flushAutomatically garantiza que todos los cambios previos
        // están en BD antes del DELETE; clearAutomatically previene entidades huérfanas)
        cartRepository.deleteAllByUserId(buyerId);

        createShipmentRecord(request, buyer, order.getId(), orderNumber);

        // Notificar al comprador
        notificationService.notifyPurchaseConfirmed(buyer, order);

        // Notificar a cada vendedor involucrado en la orden
        sellerIds.forEach(sellerId -> userRepository.findById(sellerId)
                .ifPresent(seller -> notificationService.notifySellerNewOrder(seller, order)));

        log.info("Orden {} creada para comprador {}", orderNumber, buyerId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTOs.OrderResponse getOrderById(Long orderId, Long requestingUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", orderId));
        validateOrderAccess(order, requestingUserId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTOs.OrderResponse getOrderByNumber(String orderNumber, Long requestingUserId) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "número", orderNumber));
        validateOrderAccess(order, requestingUserId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDTOs.OrderResponse> getBuyerOrders(Long buyerId, Pageable pageable) {
        Page<OrderDTOs.OrderResponse> page = orderRepository
                .findByBuyerIdOrderByCreatedAtDesc(buyerId, pageable)
                .map(orderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDTOs.OrderResponse> getSellerOrders(Long sellerId, Pageable pageable) {
        Page<OrderDTOs.OrderResponse> page = orderRepository
                .findOrdersBySellerId(sellerId, pageable)
                .map(orderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDTOs.OrderResponse> getAllOrders(Pageable pageable) {
        Page<OrderDTOs.OrderResponse> page = orderRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(orderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDTOs.OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<OrderDTOs.OrderResponse> page = orderRepository
                .findByStatus(status, pageable)
                .map(orderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDTOs.OrderResponse> searchOrders(String keyword, Pageable pageable) {
        Page<OrderDTOs.OrderResponse> page = orderRepository
                .searchOrders(keyword, pageable)
                .map(orderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public OrderDTOs.OrderResponse updateOrderStatus(Long orderId,
                                                     OrderDTOs.UpdateOrderStatusRequest request, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", orderId));

        order.setStatus(request.getStatus());
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }
        orderRepository.save(order);

        // Notificar al comprador según el nuevo estado
        if (request.getStatus() == OrderStatus.SHIPPED) {
            notificationService.notifyShipmentUpdate(order.getBuyer(), order);
        } else if (request.getStatus() == OrderStatus.DELIVERED) {
            notificationService.notifyOrderDelivered(order.getBuyer(), order);
        } else if (request.getStatus() == OrderStatus.CANCELLED) {
            notificationService.notifyOrderCancelled(order.getBuyer(), order);
        }

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", orderId));

        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new ForbiddenException("Solo puedes cancelar tus propias órdenes");
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("La orden no se puede cancelar en estado: " + order.getStatus());
        }

        order.getItems().forEach(item -> {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
            productRepository.save(p);
        });

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        notificationService.notifyOrderCancelled(order.getBuyer(), order);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateOrderAccess(Order order, Long userId) {
        boolean isBuyer = order.getBuyer().getId().equals(userId);
        boolean isSeller = order.getItems().stream()
                .anyMatch(i -> i.getSellerId().equals(userId));
        if (!isBuyer && !isSeller) {
            throw new ForbiddenException("Acceso denegado a esta orden");
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
        if (coupon.getMinimumOrderAmount() != null
                && subtotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new BusinessException("La orden no cumple el monto mínimo para este cupón");
        }
        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (coupon.getMaximumDiscount() != null) {
                discount = discount.min(coupon.getMaximumDiscount());
            }
        } else {
            discount = coupon.getDiscountValue();
        }
        return discount.min(subtotal);
    }

    private List<OrderItem> buildOrderItems(List<CartItem> cartItems) {
        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            items.add(OrderItem.builder()
                    .product(p).productTitle(p.getTitle())
                    .productImage(p.getImages().isEmpty() ? null : p.getImages().get(0))
                    .unitPrice(p.getPrice()).quantity(ci.getQuantity())
                    .sellerId(p.getSeller().getId())
                    .sellerName(p.getSeller().getFirstName() + " " + p.getSeller().getLastName())
                    .build());
        }
        return items;
    }

    private BigDecimal calculateShippingCost(OrderDTOs.CreateOrderRequest req) {
        return shippingService.getStrategy(req.getShippingProvider())
                .calculateShipping(ShippingStrategy.ShippingRequest.builder()
                        .destinationCity(req.getShippingCity())
                        .destinationCountry(req.getShippingCountry())
                        .destinationZip(req.getShippingZip()).weightKg(1.0).build())
                .getCost();
    }
    private void createShipmentRecord(OrderDTOs.CreateOrderRequest req, User buyer, Long orderId, String orderNumber) {
        try {
            ShippingStrategy.ShipmentCreationRequest shipmentReq = ShippingStrategy.ShipmentCreationRequest.builder()
                    .shippingRequest(ShippingStrategy.ShippingRequest.builder()
                            .destinationAddress(req.getShippingAddress())
                            .destinationCity(req.getShippingCity())
                            .destinationCountry(req.getShippingCountry())
                            .destinationZip(req.getShippingZip())
                            .weightKg(1.0)
                            .build())
                    .recipientName(buyer.getFirstName() + " " + buyer.getLastName())
                    .recipientPhone(buyer.getPhoneNumber())
                    .recipientEmail(buyer.getEmail())
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .build();
            shippingService.createShipment(req.getShippingProvider(), shipmentReq);
        } catch (Exception e) {
            log.warn("Could not create shipment record for order {}: {}", orderNumber, e.getMessage());
        }
    }

    private String generateOrderNumber() {
        // Epoch millis (13 digits) + 8 uppercase hex chars from UUID
        // Collision probability: effectively zero
        String millis = String.valueOf(System.currentTimeMillis());
        String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + millis + "-" + uid;
    }
}
