package dev.ai4j.aid2;

import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.ChatHistory;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.chat.SimpleChatHistory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static dev.ai4j.chat.AiMessage.aiMessage;
import static dev.ai4j.model.openai.OpenAiModelName.*;

public class Conversation {

    private static ChatHistory HISTORY;
    private static StringBuffer ANSWER = new StringBuffer();
    private static final AtomicLong LATEST_APPENDER = new AtomicLong();
    private static final AtomicBoolean CURRENTLY_STREAMING = new AtomicBoolean(false);
    private static final Map<Long, Boolean> ANSWER_STOPPED = new ConcurrentHashMap<>();

    static {
        reset();
    }

    public static void reset() {
        stopIfStreaming();
        HISTORY = SimpleChatHistory.builder()
                .capacityInTokens(getContextSize(Config.model()))
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

        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(Config.model())
                .apiKey(Config.openAiApiKey())
                .temperature(Config.temperature())
                .timeout(Duration.ofMinutes(10))
                .build();

        System.out.println("================== OLOLO ==================");
        System.out.println(Config.model());
        System.out.println(Config.temperature());
        System.out.println(Config.openAiApiKey());
        System.out.println(HISTORY.history().size());

        ANSWER = new StringBuffer();

        long id = System.currentTimeMillis();
        LATEST_APPENDER.set(id);
        ANSWER_STOPPED.put(id, false);

        model.chat(HISTORY.history(), new StreamingResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {

                CURRENTLY_STREAMING.set(true);

                if (ANSWER_STOPPED.get(id)) {
                    return;
                }

                handler.onPartialResponse(partialResponse);
                ANSWER.append(partialResponse);
            }

            @Override
            public void onComplete() {
                CURRENTLY_STREAMING.set(false);

                if (ANSWER_STOPPED.get(id)) {
                    System.out.println("OLOLO IGNORED APPENDING ANSWER ON COMPLETE");
                    return;
                }

                System.out.println("OLOLO APPENDED COMPLETE ANSWER: " + ANSWER.toString());
                HISTORY.add(aiMessage(ANSWER.toString()));
            }

            @Override
            public void onError(Throwable error) {
                CURRENTLY_STREAMING.set(false);

                handler.onError(error);
            }
        });
    }

    public static void stopIfStreaming() {

        if (!CURRENTLY_STREAMING.get()) {
            return;
        }

        ANSWER_STOPPED.put(LATEST_APPENDER.get(), true);
        System.out.println("OLOLO STOPPED!!!");

        if (ANSWER.isEmpty()) {
            System.out.println("OLOLO IGNORED EMPTY ANSWER");
            return;
        }
        HISTORY.add(aiMessage(ANSWER.toString()));
        System.out.println("OLOLO APPENDED PARTIAL ANSWER: " + ANSWER.toString());

        ANSWER = new StringBuffer();
    }
}
