package com.mercaduca.products.repository;

import com.mercaduca.common.enums.ProductStatus;
import com.mercaduca.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findBySellerIdAndStatus(Long sellerId, ProductStatus status, Pageable pageable);

    Page<Product> findByCategoryIdAndStatus(Long categoryId, ProductStatus status, Pageable pageable);

    Page<Product> findByFeaturedTrueAndStatus(ProductStatus status, Pageable pageable);

    List<Product> findBySellerIdAndStatus(Long sellerId, ProductStatus status);

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.totalSold DESC")
    List<Product> findTopSelling(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.viewCount DESC")
    List<Product> findTopViewed(Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.averageRating = :avg, p.totalReviews = :count WHERE p.id = :id")
    void updateRatingStats(@Param("id") Long id, @Param("avg") Double avg, @Param("count") int count);
}
