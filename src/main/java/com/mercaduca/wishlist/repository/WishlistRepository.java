package com.mercaduca.wishlist.repository;
import com.mercaduca.wishlist.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    Page<WishlistItem> findByUserId(Long userId, Pageable pageable);
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    long countByUserId(Long userId);
}
