# Agent Tool Contract

This document defines deterministic capabilities exposed to LangChain4j through `CustomerSupportTools`.

## Read tools

### getCustomer
Use when customer identity information is required. Input: customer UUID.

### getOrder
Use when order status, amount, customer, or delivery dates are required.

### getDeliveryStatus
Use when delivery state and delay duration are required.

### getPayment
Use when payment state and amount must be checked.

### checkRefundPolicy
Use before requesting a refund. The backend evaluates authoritative business rules.

### getSupportTicket
Use when an existing support ticket must be retrieved.

## Write tools

### requestRefund
Input:

```text
orderNumber
reason
idempotencyKey
```

Possible outcomes:

```text
REFUNDED
PENDING_HUMAN_APPROVAL
REJECTED
```

The tool validates policy and payment state before routing the request through the controlled approval workflow.

### createSupportTicket
Creates a deterministic support ticket using customer and optional order identifiers.

## Recommended refund sequence

```text
getOrder
   -> getDeliveryStatus
   -> getPayment
   -> checkRefundPolicy
   -> requestRefund
```

The agent should not infer refund eligibility from conversation text when the backend can make the authoritative decision.

## Boundary

```text
LLM reasoning
    |
    v
Tool selection
    |
    v
Backend validation
    |
    v
Database transaction
```

The LLM is not the source of truth for payment state, refund eligibility, approval state, or idempotency.