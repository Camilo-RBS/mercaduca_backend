package com.mercaduca.orders.repository;

import com.mercaduca.orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    boolean existsByOrderBuyerIdAndProductId(Long buyerId, Long productId);

    @Query(value = "SELECT product_id, SUM(quantity) as total FROM order_items " +
                   "GROUP BY product_id ORDER BY total DESC LIMIT :lim", nativeQuery = true)
    List<Object[]> findTopSellingProductIds(int lim);
}
