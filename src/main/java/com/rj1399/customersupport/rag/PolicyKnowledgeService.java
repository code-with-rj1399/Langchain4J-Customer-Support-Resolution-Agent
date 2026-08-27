package com.rj1399.customersupport.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PolicyKnowledgeService {
    private final EmbeddingStore<TextSegment> store;
    private final EmbeddingModel model;
    private final ResourcePatternResolver resolver;
    private final boolean ragEnabled;
    private final int topK;
    private final double similarityThreshold;

    public PolicyKnowledgeService(EmbeddingStore<TextSegment> store,
                                  EmbeddingModel model,
                                  ResourcePatternResolver resolver,
                                  @Value("${rag.enabled:true}") boolean ragEnabled,
                                  @Value("${rag.top-k:4}") int topK,
                                  @Value("${rag.similarity-threshold:0.60}") double similarityThreshold) {
        this.store = store;
        this.model = model;
        this.resolver = resolver;
        this.ragEnabled = ragEnabled;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
    }

    @PostConstruct
    public void indexPolicies() {
        if (!ragEnabled) {
            return;
        }

        try {
            for (Resource resource : resolver.getResources("classpath:/knowledge/*.md")) {
                String source = Objects.requireNonNullElse(resource.getFilename(), "unknown");
                String text = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                Map<String, Object> metadataValues = new HashMap<>();
                metadataValues.put("source", source);
                metadataValues.put("knowledgeBase", "customer-support-policy");
                metadataValues.put("documentType", "policy");

                Document document = Document.from(text, Metadata.from(metadataValues));
                List<TextSegment> segments = DocumentSplitters.recursive(1200, 200).split(document);

                for (TextSegment segment : segments) {
                    store.add(model.embed(segment).content(), segment);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize policy knowledge base", e);
        }
    }

    public KnowledgeSearchResult search(String query) {
        long started = System.nanoTime();
        if (!ragEnabled) {
            return new KnowledgeSearchResult(query, List.of(), elapsed(started));
        }

        Embedding queryEmbedding = model.embed(query).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(topK)
                .minScore(similarityThreshold)
                .build();
        EmbeddingSearchResult<TextSegment> result = store.search(request);
        List<EmbeddingMatch<TextSegment>> matches = result.matches();

        return new KnowledgeSearchResult(
                query,
                matches.stream()
                        .map(match -> new KnowledgeMatch(
                                match.embedded().metadata().getString("source"),
                                match.score(),
                                match.embedded().text()))
                        .toList(),
                elapsed(started));
    }

    private long elapsed(long started) {
        return (System.nanoTime() - started) / 1_000_000;
    }

    public record KnowledgeSearchResult(String query, List<KnowledgeMatch> matches, long durationMs) {
        public String context() {
            return matches.stream()
                    .map(match -> "Source: " + match.source() + "\n" + match.content())
                    .collect(Collectors.joining("\n\n---\n\n"));
        }
    }

    public record KnowledgeMatch(String source, double score, String content) {
    }
}
