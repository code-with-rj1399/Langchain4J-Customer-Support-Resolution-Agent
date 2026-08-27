# Agent Safety Boundary

## Core principle

> **The agent owns reasoning. The backend owns truth.**

The LLM can interpret intent, select tools, retrieve policy context, and formulate a response. It does not directly access JPA repositories or write database state.

## Financial action flow

```text
LLM / RAG
   |
   v
requestRefund()
   |
   v
Tool guardrail
   |
   v
CustomerSupportService
   |
   +--> delivery delay
   +--> payment state
   +--> existing refund
   +--> idempotency
   |
   v
RefundApprovalService
   |
   +--> automatic path
   +--> human approval path
```

## Why RAG is not authoritative

Retrieved documents can be stale, incomplete, or irrelevant. They provide explanatory context, while deterministic Java services enforce the actual business rule.

## Idempotency

Refund creation requires an idempotency key. This is important when an agent retry or network failure makes an earlier operation's result ambiguous.

## Key lesson

Safety does not come from telling the model to behave correctly. Safety-critical state changes must pass deterministic backend controls.
