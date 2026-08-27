# Agent UI

## Purpose

The web UI is a portfolio and learning surface for comparing the two LangChain4j architectures implemented in this repository.

## Architecture selector

The user can switch between:

```text
Single Agent
Multi-Agent
```

The selected mode routes the request to:

```text
Single Agent -> POST /api/agent/resolve
Multi-Agent  -> POST /api/multi-agent/resolve
```

## Response presentation

The UI provides order input, customer-message input, loading/progress state, response rendering, and basic Markdown-style formatting for bold text and lists.

## Execution visibility

The UI displays high-level progress events. It intentionally does not display hidden model reasoning. Authoritative execution events should come from the backend trace infrastructure rather than client-side guesses.

## Static hosting

The UI is implemented as Spring Boot static resources:

```text
src/main/resources/static/
├── index.html
├── app.js
└── app.css
```

When Spring Boot is running, the demo is available from the application root.
