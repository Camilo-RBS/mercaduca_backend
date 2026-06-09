package com.mercaduca.warnings.repository;

import com.mercaduca.warnings.entity.SellerWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerWarningRepository extends JpaRepository<SellerWarning, Long> {
    List<SellerWarning> findBySellerId(Long sellerId);
    long countBySellerId(Long sellerId);
}
