package com.mercaduca.orders.service;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.common.enums.OrderStatus;
import com.mercaduca.orders.dto.OrderDTOs;
import org.springframework.data.domain.Pageable;
public interface OrderService {
    OrderDTOs.OrderResponse createOrder(OrderDTOs.CreateOrderRequest request, Long buyerId);
    OrderDTOs.OrderResponse getOrderById(Long orderId, Long requestingUserId);
    OrderDTOs.OrderResponse getOrderByNumber(String orderNumber, Long requestingUserId);
    PageResponse<OrderDTOs.OrderResponse> getBuyerOrders(Long buyerId, Pageable pageable);
    PageResponse<OrderDTOs.OrderResponse> getSellerOrders(Long sellerId, Pageable pageable);
    PageResponse<OrderDTOs.OrderResponse> getAllOrders(Pageable pageable);
    PageResponse<OrderDTOs.OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);
    OrderDTOs.OrderResponse updateOrderStatus(Long orderId, OrderDTOs.UpdateOrderStatusRequest request, Long userId);
    void cancelOrder(Long orderId, Long buyerId);
    PageResponse<OrderDTOs.OrderResponse> searchOrders(String keyword, Pageable pageable);
}
