package dev.ai4j.aid2.explain.comment;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Config;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.chat.SystemMessage.systemMessage;
import static dev.ai4j.chat.UserMessage.userMessage;

public class AiCodeCommenter {

    private final String modelName;

    public AiCodeCommenter(String modelName) {
        this.modelName = modelName;
    }

    public void coverWithComments(String code, StreamingResponseHandler handler) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(Config.openAiApiKey())
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();

        PromptTemplate template = PromptTemplate.from(Config.coverWithCommentsPromptTemplate());

        List<ChatMessage> messages = List.of(
                systemMessage("You are a senior Java software engineer that explains and comments the code well."), // TODO needed?
                userMessage(template.format(Map.of("code", code)))
        );

        model.chat(messages, handler);
    }
}
