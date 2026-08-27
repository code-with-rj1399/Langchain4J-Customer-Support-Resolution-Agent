# Multi-Agent Architecture

## Goal

Split responsibilities when one agent becomes too broad. The current implementation uses a supervisor and specialist responsibilities.

```text
Customer
   |
   v
MultiAgentSupervisor
   |
   +--> Investigation
   |      +--> getOrder
   |      +--> getDeliveryStatus
   |      +--> getPayment
   |
   +--> Resolution
   |      +--> PolicyKnowledgeService.search
   |      +--> checkRefundPolicy
   |
   +--> Communication Agent
          +--> LangChain4j AiServices
          +--> customer-facing response
```

## Typed contracts

The supervisor exchanges typed records rather than giving every component unrestricted database access:

```text
AgentTask
   -> Investigation
   -> Resolution
   -> AgentResult
```

## Why multiple agents

Specialization creates clearer responsibility boundaries and makes testing, tracing, and future approval policies easier to understand. More agents are not automatically better; each agent should have a meaningful responsibility boundary.

## Endpoint

```http
POST /api/multi-agent/resolve
```

Example request:

```json
{
  "orderNumber": "1002",
  "customerMessage": "My order is delayed. Can I get a refund?"
}
```

## Portfolio takeaway

The project demonstrates the difference between a tool-calling single agent and a supervisor workflow with specialized responsibilities.
