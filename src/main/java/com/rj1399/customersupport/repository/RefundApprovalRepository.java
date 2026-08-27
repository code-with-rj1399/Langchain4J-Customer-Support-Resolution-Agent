package com.rj1399.customersupport.repository;
import com.rj1399.customersupport.domain.RefundApproval; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RefundApprovalRepository extends JpaRepository<RefundApproval,UUID>{ List<RefundApproval> findByStatusOrderByCreatedAtAsc(RefundApproval.Status status); Optional<RefundApproval> findByIdempotencyKey(String idempotencyKey); }
