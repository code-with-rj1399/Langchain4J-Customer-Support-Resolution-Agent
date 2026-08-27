package com.rj1399.customersupport.agent;
import com.rj1399.customersupport.api.ApiDtos; import com.rj1399.customersupport.rag.PolicyKnowledgeService; import org.springframework.stereotype.Service;
@Service
public class SpecialistAgents {
 private final CustomerSupportTools tools; private final PolicyKnowledgeService knowledge;
 public SpecialistAgents(CustomerSupportTools tools,PolicyKnowledgeService knowledge){this.tools=tools;this.knowledge=knowledge;}
 public Investigation investigate(String orderNumber){return new Investigation(tools.getOrder(orderNumber),tools.getDeliveryStatus(orderNumber),tools.getPayment(orderNumber));}
 public Resolution resolve(String orderNumber,String request){ApiDtos.RefundPolicyResponse policy=tools.checkRefundPolicy(orderNumber);return new Resolution(policy,knowledge.search(request).context());}
 public String communicate(Investigation i,Resolution r){return "Order "+i.order().orderNumber()+" is currently "+i.order().status()+". "+r.policy().rule()+"\n\nRelevant policy information:\n"+r.knowledgeContext();}
 public record Investigation(ApiDtos.OrderResponse order,ApiDtos.DeliveryResponse delivery,ApiDtos.PaymentResponse payment){}
 public record Resolution(ApiDtos.RefundPolicyResponse policy,String knowledgeContext){}
}
