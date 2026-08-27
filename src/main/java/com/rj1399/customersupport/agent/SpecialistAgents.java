package com.rj1399.customersupport.agent;

import com.rj1399.customersupport.api.ApiDtos;
import com.rj1399.customersupport.rag.PolicyKnowledgeService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

@Service
public class SpecialistAgents {
    private final CustomerSupportTools tools;
    private final PolicyKnowledgeService knowledge;
    private final CommunicationAgent communicationAgent;

    public SpecialistAgents(CustomerSupportTools tools, PolicyKnowledgeService knowledge, ChatLanguageModel model) {
        this.tools = tools;
        this.knowledge = knowledge;
        this.communicationAgent = AiServices.builder(CommunicationAgent.class)
                .chatLanguageModel(model)
                .systemMessageProvider(id -> "You are the Communication Agent. Produce a concise, empathetic customer response using only supplied investigation and policy facts. Never invent backend state or claim a pending approval is complete.")
                .build();
    }

    public Investigation investigate(String orderNumber) {
        return new Investigation(tools.getOrder(orderNumber), tools.getDeliveryStatus(orderNumber), tools.getPayment(orderNumber));
    }

    public Resolution resolve(String orderNumber, String request) {
        ApiDtos.RefundPolicyResponse policy = tools.checkRefundPolicy(orderNumber);
        return new Resolution(policy, knowledge.search(request).context());
    }

    public String communicate(Investigation investigation, Resolution resolution) {
        String facts = "Order=" + investigation.order().orderNumber()
                + "\nStatus=" + investigation.order().status()
                + "\nDaysLate=" + investigation.delivery().daysLate()
                + "\nPaymentStatus=" + investigation.payment().status()
                + "\nAuthoritativePolicy=" + resolution.policy().rule()
                + "\nRelevantKnowledge=" + resolution.knowledgeContext();
        return communicationAgent.respond(facts);
    }

    interface CommunicationAgent { String respond(String facts); }
    public record Investigation(ApiDtos.OrderResponse order, ApiDtos.DeliveryResponse delivery, ApiDtos.PaymentResponse payment) {}
    public record Resolution(ApiDtos.RefundPolicyResponse policy, String knowledgeContext) {}
}
