package com.mercaduca.disputes.repository;

import com.mercaduca.disputes.entity.Dispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    /** Solo disputas activas (excluye ARCHIVED) */
    Page<Dispute> findByBuyerIdAndStatusNot(Long buyerId, Dispute.DisputeStatus status, Pageable pageable);

    /** Compatibilidad con llamadas genéricas por status */
    Page<Dispute> findByBuyerId(Long buyerId, Pageable pageable);

    Page<Dispute> findByStatus(Dispute.DisputeStatus status, Pageable pageable);

    /** Todas las disputas activas (no archivadas) */
    Page<Dispute> findByStatusNot(Dispute.DisputeStatus status, Pageable pageable);


    boolean existsByOrderIdAndBuyerId(Long orderId, Long buyerId);

    /**
     * Disputas activas cuyas órdenes contienen items del vendedor dado.
     */
    @Query("SELECT DISTINCT d FROM Dispute d " +
           "JOIN d.order o JOIN o.items i " +
           "WHERE i.sellerId = :sellerId AND d.status <> 'ARCHIVED' " +
           "ORDER BY d.createdAt DESC")
    Page<Dispute> findBySellerIdInOrderItems(@Param("sellerId") Long sellerId, Pageable pageable);

    /**
     * Para el scheduler: disputas resueltas hace más de 48h.
     */
    List<Dispute> findByStatusInAndUpdatedAtBefore(
            List<Dispute.DisputeStatus> statuses, LocalDateTime cutoff);
}
