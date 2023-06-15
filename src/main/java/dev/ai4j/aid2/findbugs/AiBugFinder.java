package dev.ai4j.aid2.findbugs;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Config;
import dev.ai4j.chat.UserMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiBugFinder {

    private final String modelName;

    public AiBugFinder(String modelName) {
        this.modelName = modelName;
    }

    public void findBugs(String code, StreamingResponseHandler handler) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(Config.openAiApiKey())
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();

        PromptTemplate template = PromptTemplate.from(Config.findBugsPromptTemplate());

        UserMessage userMessage = userMessage(template.format("code", code));

        model.chat(List.of(userMessage), handler);
    }
}
