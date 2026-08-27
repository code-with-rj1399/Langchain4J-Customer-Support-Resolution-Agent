# Guardrails and Prompt-Injection Defense

The agent uses defense in depth rather than relying on the system prompt alone.

## Security boundary

```text
Customer input
     |
     v
PromptInjectionGuardrail
     |
     +--> blocked
     |
     v
LangChain4j Agent / LLM
     |
     +--> RAG (untrusted data)
     |
     +--> tools
             |
             v
       ToolExecutionGuardrail
             |
             v
       Backend policy + HITL
```

## Input guardrail

`PromptInjectionGuardrail` runs before the model invocation. It enforces:

- Non-empty input
- Maximum input length of 8,000 characters
- Detection of common attempts to ignore instructions
- Detection of attempts to reveal system prompts or developer messages
- Detection of attempts to bypass approval or security controls
- Basic jailbreak detection

Pattern matching is a first defensive layer, not a guarantee that every injection can be detected.

## Tool boundary

`ToolExecutionGuardrail` identifies high-risk refund tools and validates required arguments such as order number and refund reason.

The final authority remains deterministic backend code. The model cannot disable business validation through prompt content.

## Untrusted data boundary

Customer input, tool output, and retrieved RAG content are treated as data, not executable instructions.

## Production hardening

A production system should additionally consider authentication, per-customer authorization, approver roles, rate limiting, PII detection, tool allowlists, semantic injection classifiers, adversarial tests, and monitoring for repeated blocked attempts.
