package dev.ai4j.aid2;

import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.ChatHistory;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.chat.ChatModel;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.chat.SimpleChatHistory;

import java.time.Duration;

import static dev.ai4j.chat.AiMessage.aiMessage;
import static dev.ai4j.model.openai.OpenAiModelName.*;

public class Conversation {

    private static ChatModel MODEL;
    private static ChatHistory HISTORY;
    static {
        reset(GPT_4); // TODO make configurable
    }

    public static void reset(String modelName) {
        MODEL = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(Config.openAiApiKey())
                .temperature(0.0) // TODO make configurable via UI
                .timeout(Duration.ofMinutes(10))
                .build();
        HISTORY = SimpleChatHistory.builder()
                .capacityInTokens(getContextSize(modelName))
                .build();
    }

    private static int getContextSize(String modelName) {
        if (modelName.startsWith(GPT_4_32K)) {
            return 32000;
        }

        if (modelName.startsWith(GPT_4)) {
            return 8000;
        }

        if (modelName.startsWith("gpt-3.5-turbo-16k")) {
            return 16000;
        }

        if (modelName.startsWith(GPT_3_5_TURBO)) {
            return 4000;
        }

        throw new IllegalArgumentException("Unknown model: " + modelName);
    }

    public static void fromUser(ChatMessage message, StreamingResponseHandler handler) {
        HISTORY.add(message);

        MODEL.chat(HISTORY.history(), new StreamingResponseHandler() {

            private final StringBuilder sb = new StringBuilder();

            @Override
            public void onPartialResponse(String partialResponse) {
                handler.onPartialResponse(partialResponse);
                sb.append(partialResponse);
            }

            @Override
            public void onComplete() {
                HISTORY.add(aiMessage(sb.toString()));
            }

            @Override
            public void onError(Throwable error) {
                handler.onError(error);
            }
        });
    }
}
