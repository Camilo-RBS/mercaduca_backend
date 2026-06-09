package com.mercaduca.reviews.repository;

import com.mercaduca.reviews.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    boolean existsByBuyerIdAndProductIdAndOrderId(Long buyerId, Long productId, Long orderId);

    Optional<Review> findByBuyerIdAndProductId(Long buyerId, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.seller.id = :sellerId")
    Double findAverageRatingBySellerId(@Param("sellerId") Long sellerId);

    long countByProductId(Long productId);
}
