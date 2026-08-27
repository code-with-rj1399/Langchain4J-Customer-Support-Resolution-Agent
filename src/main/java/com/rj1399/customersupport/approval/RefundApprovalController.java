package com.rj1399.customersupport.approval;
import com.rj1399.customersupport.domain.RefundApproval; import com.rj1399.customersupport.service.RefundApprovalService; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.time.Instant; import java.util.*;
@RestController @RequestMapping("/api/approvals")
public class RefundApprovalController {
 private final RefundApprovalService service; public RefundApprovalController(RefundApprovalService service){this.service=service;}
 @GetMapping("/pending") public List<ApprovalResponse> pending(){return service.pending().stream().map(this::map).toList();}
 @PostMapping("/{id}/approve") public ApprovalResponse approve(@PathVariable UUID id,@RequestBody DecisionRequest r){service.approve(id,r.decidedBy(),r.reason()); return service.pending().stream().filter(a->a.getId().equals(id)).findFirst().map(this::map).orElse(new ApprovalResponse(id,"APPROVED",null,null,null));}
 @PostMapping("/{id}/reject") public ApprovalResponse reject(@PathVariable UUID id,@RequestBody DecisionRequest r){service.reject(id,r.decidedBy(),r.reason());return new ApprovalResponse(id,"REJECTED",null,null,null);}
 private ApprovalResponse map(RefundApproval a){return new ApprovalResponse(a.getId(),a.getStatus().name(),a.getOrder().getOrderNumber(),a.getAmount(),a.getReason());}
 public record DecisionRequest(String decidedBy,String reason){} public record ApprovalResponse(UUID id,String status,String orderNumber,BigDecimal amount,String reason){}
}
