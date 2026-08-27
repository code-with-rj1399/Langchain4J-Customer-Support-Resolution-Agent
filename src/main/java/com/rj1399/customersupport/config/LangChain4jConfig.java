package com.rj1399.customersupport.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${OPENAI_API_KEY:}") String apiKey,
            @Value("${agent.open-ai.model:gpt-5.4-nano}") String modelName) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is required when the LangChain4j agent is enabled");
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
