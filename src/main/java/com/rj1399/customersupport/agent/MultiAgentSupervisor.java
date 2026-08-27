package com.rj1399.customersupport.agent;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MultiAgentSupervisor {

    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile(
            "\\b(?:order\\s*(?:number|no\\.?|#)?\\s*)?(#?\\d{4,})\\b",
            Pattern.CASE_INSENSITIVE);

    private final SpecialistAgents agents;

    public MultiAgentSupervisor(SpecialistAgents agents) {
        this.agents = agents;
    }

    public AgentResult resolve(String customerMessage) {
        String orderNumber = extractOrderNumber(customerMessage);
        if (orderNumber == null) {
            return new AgentResult(null, null,
                    "Please provide your order number in the message so I can investigate the request.");
        }

        SpecialistAgents.Investigation investigation = agents.investigate(orderNumber);
        SpecialistAgents.Resolution resolution = agents.resolve(orderNumber, customerMessage);
        return new AgentResult(
                investigation,
                resolution,
                agents.communicate(investigation, resolution));
    }

    private String extractOrderNumber(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }

        Matcher matcher = ORDER_NUMBER_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }

        String value = matcher.group(1);
        return value.startsWith("#") ? value.substring(1) : value;
    }

    public record AgentResult(SpecialistAgents.Investigation investigation,
                              SpecialistAgents.Resolution resolution,
                              String response) {
    }
}
