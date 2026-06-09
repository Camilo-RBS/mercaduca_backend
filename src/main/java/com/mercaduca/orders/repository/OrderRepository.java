package com.mercaduca.orders.repository;
import com.mercaduca.common.enums.OrderStatus;
import com.mercaduca.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.sellerId = :sellerId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);
    @Query("SELECT COALESCE(SUM(i.unitPrice * i.quantity), 0) FROM OrderItem i WHERE i.sellerId = :sellerId")
    BigDecimal getTotalRevenueForSeller(@Param("sellerId") Long sellerId);
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalPlatformRevenue();
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    List<Order> findOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    long countByStatus(OrderStatus status);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
