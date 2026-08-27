# Single-Agent Orchestration

## Architecture

```text
Customer
   |
   v
LangChainAgentOrchestrator
   |
   +--> LangChain4j AiServices
   |
   +--> Typed tools
          |
          +--> Customer
          +--> Order
          +--> Delivery
          +--> Payment
          +--> Refund policy
          +--> Refund request / HITL
          +--> Support ticket
```

The orchestrator validates the customer message, creates an execution trace, binds the execution ID to the tool context, and invokes a LangChain4j AI service. LangChain4j drives the model/tool interaction.

## Responsibility boundary

The agent owns intent understanding, planning, tool selection, sequencing, and response generation.

The backend owns database state, validation, business rules, idempotency, approval state, and financial mutations.

## Current endpoint

The repository should expose the single-agent controller using the same conceptual contract:

```http
POST /api/agent/resolve
```

Example request:

```json
{"message":"My order is delayed. Can I get a refund?"}
```

The exact tool sequence is selected by the model. The system prompt requires backend facts and deterministic policy checks to remain authoritative.

## Why start here

A single agent is easier to reason about and provides the baseline against which the multi-agent architecture can be compared.
