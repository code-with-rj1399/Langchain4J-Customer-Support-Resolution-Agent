package com.rj1399.customersupport.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    @Bean
    EmbeddingModel embeddingModel(
            @Value("${OPENAI_API_KEY:}") String apiKey,
            @Value("${agent.open-ai.embedding-model:text-embedding-3-small}") String modelName) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is required when RAG uses OpenAI embeddings");
        }
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(
            @Value("${spring.datasource.url}") String jdbcUrl,
            @Value("${spring.datasource.username}") String user,
            @Value("${spring.datasource.password}") String password,
            @Value("${agent.open-ai.embedding-model:text-embedding-3-small}") String embeddingModel) {
        String url = jdbcUrl.replace("jdbc:", "");
        java.net.URI uri = java.net.URI.create(url);
        String database = uri.getPath().replaceFirst("/", "");
        int dimension = "text-embedding-3-large".equals(embeddingModel) ? 3072 : 1536;
        return PgVectorEmbeddingStore.builder()
                .host(uri.getHost())
                .port(uri.getPort() == -1 ? 5432 : uri.getPort())
                .database(database)
                .user(user)
                .password(password)
                .table("policy_embeddings")
                .dimension(dimension)
                .createTable(true)
                .build();
    }
}
