# LangChain4J Customer Support Resolution Agent

LangChain4j implementation of the Spring AI customer-support agent. The project mirrors the original business workflow while replacing Spring AI chat/tool integration with LangChain4j.

## Parity goals

- Customer, order, delivery, payment and support-ticket lookup tools
- Deterministic refund-policy enforcement
- Retrieval-augmented policy knowledge
- Prompt-injection guardrails
- Tool-execution guardrails
- Human approval for high-value refunds
- Agent orchestration and execution traces
- Multi-agent supervisor flow
- REST APIs, PostgreSQL, Flyway and Docker support

## Framework mapping

| Spring AI | LangChain4j |
|---|---|
| `ChatClient` | `AiServices` / `ChatLanguageModel` |
| `@Tool` | `@Tool` |
| Chat client tool loop | LangChain4j AI service tool execution |
| Spring AI PGVector store | LangChain4j embedding store integration |

The implementation intentionally keeps business rules outside the LLM. The model can decide which exposed tools to call, but backend policy and approval controls remain authoritative.
