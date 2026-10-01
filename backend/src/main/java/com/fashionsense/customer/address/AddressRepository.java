package com.fashionsense.customer.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIdAsc(
            Long userId
    );

    Optional<Address>
    findByUserIdAndDefaultAddressTrue(
            Long userId
    );
}