package com.rj1399.customersupport.repository;

import com.rj1399.customersupport.domain.RefundApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefundApprovalRepository extends JpaRepository<RefundApproval, UUID> {

    @Query("select a from RefundApproval a join fetch a.order where a.status = :status order by a.createdAt asc")
    List<RefundApproval> findByStatusOrderByCreatedAtAsc(RefundApproval.Status status);

    Optional<RefundApproval> findByIdempotencyKey(String idempotencyKey);
}
