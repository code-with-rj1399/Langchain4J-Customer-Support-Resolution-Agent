# LangChain4J Customer Support Resolution Agent

A customer-support backend and **Agentic AI learning platform** built with LangChain4j, Spring Boot, typed tools, multi-agent orchestration, RAG, PGVector, deterministic business rules, guardrails, and human approval workflows.

The central architectural principle is:

> **The agent owns reasoning. The backend owns truth.**

The LLM can understand intent, select tools, retrieve knowledge, and formulate a customer response. It does **not** directly access repositories or bypass deterministic business rules.

## Feature documentation

The README gives the high-level architecture. Focused guides are available under [`docs/`](docs/README.md):

| Concept | Guide |
|---|---|
| LangChain4j Tool Calling | [`docs/tool-calling.md`](docs/tool-calling.md) |
| Single-Agent Orchestration | [`docs/single-agent.md`](docs/single-agent.md) |
| Multi-Agent Architecture | [`docs/multi-agent.md`](docs/multi-agent.md) |
| Agentic RAG / PGVector | [`docs/rag.md`](docs/rag.md) |
| Human-in-the-Loop | [`docs/human-in-the-loop.md`](docs/human-in-the-loop.md) |
| Observability | [`docs/observability.md`](docs/observability.md) |
| Safety Boundary | [`docs/safety-boundary.md`](docs/safety-boundary.md) |
| Guardrails | [`docs/guardrails.md`](docs/guardrails.md) |
| Tool Contracts | [`docs/agent-tool-contract.md`](docs/agent-tool-contract.md) |

**Recommended learning path:** Tool Calling → Single Agent → Multi-Agent → RAG → Observability → Human-in-the-Loop → Guardrails → Evaluation.

## Technology baseline

- Java 21
- Spring Boot 3.5.5
- Spring MVC
- Spring Data JPA / Hibernate
- PostgreSQL + PGVector
- Flyway
- Spring Boot Actuator
- Maven
- LangChain4j 1.0.1
- LangChain4j AI Services
- OpenAI chat-model integration
- Local AllMiniLmL6V2 embeddings

## Spring AI vs LangChain4j mapping

| Spring AI implementation | LangChain4j implementation |
|---|---|
| `ChatClient` | `AiServices` / `ChatLanguageModel` |
| Spring AI `@Tool` | LangChain4j `@Tool` |
| Chat client tool loop | LangChain4j AI service tool execution |
| Spring AI VectorStore | LangChain4j `EmbeddingStore<TextSegment>` |
| Spring AI embedding model | LangChain4j `EmbeddingModel` |
| Token splitter | `DocumentSplitters` |

## Architecture

```text
                           Customer Request
                                  |
                                  v
                         +-------------------+
                         | Input Guardrail   |
                         | Prompt Injection  |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         | LangChain4j Agent |
                         +---------+---------+
                                   |
             +---------------------+---------------------+
             |                     |                     |
             v                     v                     v
       Investigation          Resolution          Communication
             |                     |                     |
     Order / Delivery /       Policy RAG          LangChain4j AI
         Payment                    |
                                  PGVector
                                    |
                           Deterministic Backend
                                    |
                           Tool Guardrails
                                    |
                          HITL when required
                                    |
                                PostgreSQL
```

## Responsibility boundary

### Agents own

- Customer intent understanding
- Planning and delegation
- Tool selection and sequencing
- Knowledge retrieval
- Customer-facing response generation

### Backend owns

- Database state
- Business rules and validation
- Refund eligibility
- Payment and delivery state
- Idempotency
- Human approval state
- Financial mutations
- Persistence
- Security controls

RAG is not authoritative for state-changing decisions. Retrieved policy content is context; deterministic Java services decide whether an action is allowed.

## Tool calling

The agent can use deterministic capabilities including:

| Tool | Purpose |
|---|---|
| `getCustomer` | Retrieve customer facts |
| `getOrder` | Retrieve order state |
| `getDeliveryStatus` | Determine delivery status and delay |
| `getPayment` | Verify payment state and amount |
| `checkRefundPolicy` | Get authoritative refund eligibility |
| `requestRefund` | Request a controlled refund through policy/HITL checks |
| `getSupportTicket` | Retrieve an existing support ticket |
| `createSupportTicket` | Create a support ticket |

The agent never gets direct JPA repository access.

## Single Agent vs Multi-Agent

```text
Single Agent
    |
    v
LangChainAgentOrchestrator -> LangChain4j tools

Multi-Agent
    |
    v
MultiAgentSupervisor
    +--> Investigation
    +--> Resolution + RAG
    +--> Communication Agent
```

### Multi-agent endpoint

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

## Agentic RAG

Policy knowledge is stored as Markdown files under:

```text
src/main/resources/knowledge/
├── refund-policy.md
└── delivery-policy.md
```

At startup:

```text
Policy Markdown
      |
      v
Document loading
      |
      v
DocumentSplitters
      |
      v
AllMiniLmL6V2 embeddings
      |
      v
PostgreSQL / PGVector
```

At runtime:

```text
Resolution Agent
      |
      +--> PolicyKnowledgeService.search(query)
                 |
                 v
              PGVector
                 |
                 v
          Relevant policy chunks
                 |
                 v
          Grounded response context
```

## Human-in-the-Loop

Refunds follow this workflow:

```text
requestRefund()
      |
      +--> policy rejected -> REJECTED
      |
      +--> eligible
             |
             +--> amount <= ₹1,000 -> REFUNDED
             |
             +--> amount > ₹1,000
                       |
                       v
              PENDING_HUMAN_APPROVAL
                       |
                 Human decision
                   /        \
               Approve      Reject
                 |
                 v
             createRefund()
```

Approval API:

```http
GET  /api/approvals/pending
POST /api/approvals/{id}/approve
POST /api/approvals/{id}/reject
```

## Guardrails and safety

The project uses defense in depth:

```text
Customer input
      |
      v
PromptInjectionGuardrail
      |
      v
LangChain4j Agent
      |
      +--> RAG / Tool results are untrusted data
      |
      v
ToolExecutionGuardrail
      |
      v
Backend policy + authorization
      |
      +--> HITL approval when required
      |
      v
Financial mutation
```

Current prompt protection includes empty-input validation, an 8,000-character limit, and detection of common instruction-override and jailbreak patterns.

## Observability

Execution traces include the lifecycle events supported by the current implementation:

```text
AGENT_STARTED
MODEL_REQUEST
MODEL_RESPONSE
TOOL_REQUEST
TOOL_RESPONSE
TOOL_ERROR
RETRY
AGENT_COMPLETED
AGENT_ERROR
```

The trace store is an operational view and is separate from authoritative business state. It must not expose credentials, private prompts, or hidden chain-of-thought.

## Configuration

The application reads database configuration from environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/customer_support
DB_USERNAME=customer_support
DB_PASSWORD=customer_support
SERVER_PORT=8080
AGENT_ENABLED=true
```

The exact LangChain4j chat-model configuration must be supplied by the application configuration and environment before runtime use. Never commit API keys.

## Run locally

Build and test:

```bash
mvn clean verify
```

Run:

```bash
mvn spring-boot:run
```

The database must be a PostgreSQL instance with the `vector` extension available. Flyway includes the migration:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

## Business rules

The deterministic backend currently enforces:

- Refund eligibility requires at least 3 days of delivery delay.
- Payment must be `CAPTURED`.
- A completed refund cannot be created twice for the same order.
- Refund creation uses an idempotency key.
- Refunds are routed through the HITL threshold used by `RefundApprovalService`.

These rules are backend decisions. The model, RAG, and customer-provided instructions cannot override them.

## Current implementation status

Implemented through Phase 5:

- Domain model and PostgreSQL persistence
- Customer, order, delivery, payment, refund and ticket services
- LangChain4j typed tools
- Single-agent orchestration foundation
- Prompt and tool guardrails
- Execution trace infrastructure
- Retry handling for model invocation
- Human-in-the-loop refund approval
- Policy RAG with embeddings and PGVector
- Multi-agent supervisor and specialist responsibilities
- Multi-agent REST endpoint

Runtime compilation and end-to-end verification should be treated as a quality gate before declaring production readiness.

## Documentation map

```text
docs/
├── README.md
├── tool-calling.md
├── single-agent.md
├── multi-agent.md
├── rag.md
├── observability.md
├── human-in-the-loop.md
├── safety-boundary.md
├── guardrails.md
└── agent-tool-contract.md
```

For detailed explanations, start from [`docs/README.md`](docs/README.md).
