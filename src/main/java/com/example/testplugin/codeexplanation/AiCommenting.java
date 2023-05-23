package com.example.testplugin.codeexplanation;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;

public class AiCommenting {

    private static final PromptTemplate CODE_EXPLANATION_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    Here are some guidelines for writing effective comments:
                     - Use block comments for method-level and class-level documentation;
                     - Use inline comments to explain specific code segments;
                     - Begin comments with a capital letter and use proper grammar and punctuation;
                     - Be concise and to the point. Avoid unnecessary comments that simply restate the code;
                     - Explain the purpose of the code, not the code itself. Comments should focus on providing additional context or clarifying complex logic;
                     - Use comments to document important decisions, assumptions, or constraints related to the method or its behavior;
                     - Comment on tricky or non-intuitive parts of the code, including any workarounds or optimizations.
                    Please write comments to explain the methods and functionality of the following {{smellyCode}}.
                    Provide full existing code with covered with comments.
                    """);
    private final OpenAiChatModel model;

    public AiCommenting(OpenAiModelName modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void addComments(String smellyCode, ModelResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
                messageFromSystem("You are a senior Java software engineer that explains and comments on code well."),
                messageFromHuman(CODE_EXPLANATION_PROMPT_TEMPLATE.with(Map.of("smellyCode", smellyCode)))
        );
        model.chat(messages, modelResponseHandler);
    }

}
