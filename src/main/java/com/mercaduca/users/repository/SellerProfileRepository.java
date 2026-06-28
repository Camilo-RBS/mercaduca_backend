package com.mercaduca.users.repository;

import com.mercaduca.common.enums.SellerStatus;
import com.mercaduca.users.entity.SellerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Page<SellerProfile> findByStatus(SellerStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE SellerProfile sp SET sp.averageRating = :avg WHERE sp.user.id = :userId")
    void updateAverageRatingByUserId(@Param("userId") Long userId, @Param("avg") Double avg);
}
