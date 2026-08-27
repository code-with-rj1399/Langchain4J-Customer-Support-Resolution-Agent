package com.rj1399.customersupport.service;
import com.rj1399.customersupport.domain.*; import com.rj1399.customersupport.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.math.BigDecimal; import java.util.*;
@Service
public class RefundApprovalService {
 private static final BigDecimal AUTO_REFUND_LIMIT=new BigDecimal("1000.00");
 private final RefundApprovalRepository approvals; private final OrderRepository orders; private final CustomerSupportService support;
 public RefundApprovalService(RefundApprovalRepository approvals,OrderRepository orders,CustomerSupportService support){this.approvals=approvals;this.orders=orders;this.support=support;}
 @Transactional public RefundDecision request(String orderNumber,BigDecimal amount,String reason,String key,String executionId){ if(amount.compareTo(AUTO_REFUND_LIMIT)<=0) return RefundDecision.autoApproved(); return approvals.findByIdempotencyKey(key).map(RefundDecision::pending).orElseGet(()->{Order order=orders.findByOrderNumber(orderNumber).orElseThrow(()->new IllegalArgumentException("Order not found: "+orderNumber)); RefundApproval a=approvals.save(new RefundApproval(order,amount,reason,key,executionId)); return RefundDecision.pending(a);}); }
 @Transactional public void approve(UUID id,String approvedBy,String decisionReason){RefundApproval a=getPending(id); a.approve(approvedBy,decisionReason); approvals.save(a); support.createRefund(a.getOrder().getOrderNumber(),a.getReason(),a.getIdempotencyKey());}
 @Transactional public void reject(UUID id,String rejectedBy,String decisionReason){RefundApproval a=getPending(id);a.reject(rejectedBy,decisionReason);approvals.save(a);}
 @Transactional public List<RefundApproval> pending(){return approvals.findByStatusOrderByCreatedAtAsc(RefundApproval.Status.PENDING);}
 private RefundApproval getPending(UUID id){RefundApproval a=approvals.findById(id).orElseThrow(()->new IllegalArgumentException("Approval not found: "+id)); if(a.getStatus()!=RefundApproval.Status.PENDING) throw new IllegalStateException("Approval already decided"); return a;}
 public sealed interface RefundDecision permits AutoApproved, Pending { static RefundDecision autoApproved(){return new AutoApproved();} static RefundDecision pending(RefundApproval a){return new Pending(a.getId());} }
 public record AutoApproved() implements RefundDecision{} public record Pending(UUID approvalId) implements RefundDecision{}
}
