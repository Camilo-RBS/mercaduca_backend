package com.mercaduca.products.repository;
import com.mercaduca.products.entity.ProductQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {
    Page<ProductQuestion> findByProductId(Long productId, Pageable pageable);
    Page<ProductQuestion> findByProductIdAndAnsweredFalse(Long productId, Pageable pageable);
    Page<ProductQuestion> findByBuyerId(Long buyerId, Pageable pageable);
}
