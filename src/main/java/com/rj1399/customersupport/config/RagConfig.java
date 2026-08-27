package com.rj1399.customersupport.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    @Bean
    EmbeddingModel embeddingModel() { return new AllMiniLmL6V2EmbeddingModel(); }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(
            @Value("${spring.datasource.url}") String jdbcUrl,
            @Value("${spring.datasource.username}") String user,
            @Value("${spring.datasource.password}") String password) {
        String url = jdbcUrl.replace("jdbc:", "");
        java.net.URI uri = java.net.URI.create(url);
        String database = uri.getPath().replaceFirst("/", "");
        return PgVectorEmbeddingStore.builder()
                .host(uri.getHost())
                .port(uri.getPort() == -1 ? 5432 : uri.getPort())
                .database(database)
                .user(user)
                .password(password)
                .table("policy_embeddings")
                .dimension(384)
                .createTable(true)
                .build();
    }
}
