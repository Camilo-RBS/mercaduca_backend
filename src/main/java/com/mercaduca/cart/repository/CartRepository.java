package com.mercaduca.cart.repository;

import com.mercaduca.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
