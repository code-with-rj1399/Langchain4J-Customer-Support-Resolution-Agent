# Agent Observability

## Goal

Make the model and workflow lifecycle visible without exposing hidden reasoning or chain-of-thought.

## Current trace events

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

`AgentTraceStore` keeps an in-memory operational view keyed by execution ID.

## Current orchestration flow

```text
Agent request
  |
  +--> AGENT_STARTED
  +--> Prompt guardrail
  +--> MODEL_REQUEST
  +--> MODEL_RESPONSE
  +--> retry on model failure
  +--> AGENT_COMPLETED
```

## Safety

Observability should expose useful operational facts such as component names, operations, durations, statuses, and safe metadata. It must not expose API keys, credentials, private prompts, or hidden chain-of-thought.

## Current limitation

The trace infrastructure supports tool event types, but comprehensive per-tool instrumentation should be added before claiming full live observability parity. The documentation intentionally distinguishes implemented infrastructure from future enhancement.
