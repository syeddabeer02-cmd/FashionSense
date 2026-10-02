package com.fashionsense.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder>
    findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    Optional<CustomerOrder>
    findByIdAndUserId(
            Long id,
            Long userId
    );

    Optional<CustomerOrder>
    findByOrderNumberAndUserId(
            String orderNumber,
            Long userId
    );

    Optional<CustomerOrder>
    findByUserIdAndIdempotencyKey(
            Long userId,
            String idempotencyKey
    );

    @Query(
            value = """
                    SELECT pg_advisory_xact_lock(
                        hashtextextended(
                            CONCAT(
                                CAST(:userId AS TEXT),
                                ':',
                                CAST(:idempotencyKey AS TEXT)
                            ),
                            0
                        )
                    )
                    """,
            nativeQuery = true
    )
    void acquireCheckoutIdempotencyLock(
            @Param("userId") Long userId,
            @Param("idempotencyKey")
            String idempotencyKey
    );
}