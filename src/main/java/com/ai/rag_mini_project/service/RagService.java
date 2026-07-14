package com.ai.rag_mini_project.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final MessageChatMemoryAdvisor memoryAdvisor;
    private final Advisor ragAdvisor;

    public RagService(ChatClient chatClient,
                      MessageChatMemoryAdvisor memoryAdvisor,
                      Advisor ragAdvisor) {

        this.chatClient = chatClient;
        this.memoryAdvisor = memoryAdvisor;
        this.ragAdvisor = ragAdvisor;
    }

    public String getResponse(String query, String userId) {

        return chatClient
                .prompt()
                .user(query)
                .advisors(
                        memoryAdvisor,
                        ragAdvisor
                )
                .advisors(spec -> spec.param(
                        ChatMemory.CONVERSATION_ID,
                        userId
                ))
                .call()
                .content();
    }

}