# Human-in-the-Loop Approval

## Goal

Allow the agent to request a financial action while requiring explicit human authorization for higher-value refunds.

> **AI can recommend or request an action; a human authorizes high-risk actions.**

## Flow

```text
Customer
   |
   v
LangChain4j Agent
   |
   v
requestRefund()
   |
   +--> checkRefundPolicy()
   +--> getPayment()
   |
   +--> eligible amount <= ₹1,000
   |       |
   |       v
   |    REFUNDED
   |
   +--> eligible amount > ₹1,000
           |
           v
PENDING_HUMAN_APPROVAL
           |
           v
Approval API
   /                 \
Approve              Reject
   |                    |
   v                    v
createRefund()        End
```

## Approval state

```text
PENDING
   |
   +--> APPROVED
   |
   +--> REJECTED
```

Approval records are persisted in PostgreSQL. The workflow therefore survives process restarts.

## API

```http
GET  /api/approvals/pending
POST /api/approvals/{id}/approve
POST /api/approvals/{id}/reject
```

## Important safety boundary

The approval service invokes the existing controlled refund service rather than writing an arbitrary refund row directly. Backend validation and idempotency remain in the execution path.

## Demo threshold

```text
<= ₹1,000 -> automatic path
>  ₹1,000 -> human approval path
```

This threshold is intentionally simple for the learning project and should be configuration/policy driven in production.
