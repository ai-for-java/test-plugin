package dev.ai4j.aid2.ui.window;

import dev.ai4j.aid2.Config;
import dev.ai4j.chat.ChatHistory;
import dev.ai4j.chat.ChatModel;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.chat.SimpleChatHistory;

import java.time.Duration;

public class Conversation {

    private final ChatModel model;
    private final ChatHistory chatHistory;


    public Conversation(String modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(Config.openAiApiKey())
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
        this.chatHistory = SimpleChatHistory.builder()
                .capacityInTokens(4000) // todo decide based on a model name
                .build();
    }
}
