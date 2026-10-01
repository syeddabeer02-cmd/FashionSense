package com.fashionsense.customer.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIdAsc(
            Long userId
    );

    Optional<Address> findByIdAndUserId(
            Long id,
            Long userId
    );

    Optional<Address> findByUserIdAndDefaultAddressTrue(
            Long userId
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE Address a
            SET a.defaultAddress = false
            WHERE a.user.id = :userId
            AND a.defaultAddress = true
            """)
    int clearDefaultAddress(
            @Param("userId") Long userId
    );
}