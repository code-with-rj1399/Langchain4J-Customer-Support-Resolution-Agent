# Agentic RAG

## Goal

Give agents access to support policies without hard-coding every policy into prompts or Java code.

## Pipeline

```text
Markdown policies
      |
      v
Resource loading
      |
      v
LangChain4j DocumentSplitters
      |
      v
AllMiniLmL6V2 embeddings
      |
      v
PostgreSQL + PGVector
```

At runtime:

```text
Resolution specialist
  |
  +--> PolicyKnowledgeService.search(query)
             |
             v
        Query embedding
             |
             v
          PGVector
             |
             v
      Relevant policy chunks
```

## Current implementation

Policy files are stored under `src/main/resources/knowledge/`. `PolicyKnowledgeService` indexes Markdown files at startup and stores `TextSegment` embeddings with source metadata.

The current embedding model is local (`AllMiniLmL6V2EmbeddingModel`) with 384 dimensions. This avoids embedding API cost for the learning project.

## Critical safety boundary

RAG is **informational context**, not authority for a financial mutation.

```text
RAG context
   |
   v
checkRefundPolicy()
   |
   v
Deterministic backend decision
   |
   +--> requestRefund()
```

A retrieved passage cannot override payment state, refund eligibility, idempotency, or human approval.

## PGVector

The project enables the PostgreSQL `vector` extension through Flyway and configures a `PgVectorEmbeddingStore` table named `policy_embeddings`.
