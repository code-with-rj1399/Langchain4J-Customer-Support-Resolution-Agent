# Tool Calling

## What this demonstrates

The LLM does not access PostgreSQL or JPA repositories directly. It selects typed LangChain4j `@Tool` methods, while Java services execute the real business operation.

## Flow

```text
Customer request
      |
      v
LangChain4j AI Service
      |
      +--> getOrder()
      +--> getDeliveryStatus()
      +--> getPayment()
      +--> checkRefundPolicy()
      +--> requestRefund()
      |
      v
Deterministic Java service
      |
      v
PostgreSQL
```

## LangChain4j mapping

`CustomerSupportTools` is a Spring component whose methods are annotated with `dev.langchain4j.agent.tool.Tool`. The `LangChainAgentOrchestrator` registers that object with `AiServices`.

```text
AiServices
   |
   +--> CustomerSupportTools
          |
          +--> CustomerSupportService
          +--> RefundApprovalService
```

## Why this matters

Tool calling separates **reasoning from execution**. The model can decide what information or action is needed, but validation, authorization, idempotency, approval routing, and state mutation remain in the backend.

## Portfolio takeaway

This is the foundation for single-agent orchestration, multi-agent specialization, RAG, and HITL controls.
