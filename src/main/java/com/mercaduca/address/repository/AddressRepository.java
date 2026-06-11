package com.mercaduca.address.repository;
import com.mercaduca.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByDefaultAddressDesc(Long userId);
    Optional<Address> findByUserIdAndDefaultAddressTrue(Long userId);
    @Modifying @Query("UPDATE Address a SET a.defaultAddress = false WHERE a.user.id = :userId")
    void clearDefaultForUser(@Param("userId") Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
