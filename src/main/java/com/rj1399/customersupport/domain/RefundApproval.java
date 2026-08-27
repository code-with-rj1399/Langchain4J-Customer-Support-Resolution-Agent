package com.rj1399.customersupport.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="refund_approvals")
public class RefundApproval {
 public enum Status { PENDING, APPROVED, REJECTED }
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="order_id", nullable=false) private Order order;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal amount;
 @Column(nullable=false,length=300) private String reason;
 @Column(name="idempotency_key",nullable=false,unique=true,length=100) private String idempotencyKey;
 @Column(name="execution_id",length=100) private String executionId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Status status;
 private String decisionBy; private String decisionReason; @Column(nullable=false) private Instant createdAt; private Instant decidedAt;
 protected RefundApproval(){}
 public RefundApproval(Order order,BigDecimal amount,String reason,String idempotencyKey,String executionId){this.order=order;this.amount=amount;this.reason=reason;this.idempotencyKey=idempotencyKey;this.executionId=executionId;this.status=Status.PENDING;this.createdAt=Instant.now();}
 public void approve(String by,String decisionReason){this.status=Status.APPROVED;this.decisionBy=by;this.decisionReason=decisionReason;this.decidedAt=Instant.now();}
 public void reject(String by,String decisionReason){this.status=Status.REJECTED;this.decisionBy=by;this.decisionReason=decisionReason;this.decidedAt=Instant.now();}
 public UUID getId(){return id;} public Order getOrder(){return order;} public BigDecimal getAmount(){return amount;} public String getReason(){return reason;} public String getIdempotencyKey(){return idempotencyKey;} public String getExecutionId(){return executionId;} public Status getStatus(){return status;} public String getDecisionBy(){return decisionBy;} public String getDecisionReason(){return decisionReason;} public Instant getCreatedAt(){return createdAt;} public Instant getDecidedAt(){return decidedAt;}
}
