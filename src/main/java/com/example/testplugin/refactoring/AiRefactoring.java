package com.example.testplugin.refactoring;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.chat.SystemMessage.systemMessage;
import static dev.ai4j.chat.UserMessage.userMessage;

public class AiRefactoring {

    private static final PromptTemplate REFACTORING_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                       Please provide ONLY refactored code of the following {{smellyCode}}.
                       Refactoring should be based ONLY on the next requirements: {{requirements}}
                       Do NOT use any comments or explanations.
                       Do NOT change anything else in code, only required parts
                    """);

    private final OpenAiChatModel model;

    public AiRefactoring(String modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void refactor(String smellyCode, String requirements, StreamingResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
                systemMessage("you are a senior Java software engineer that refactors Java code very well. You provide only clean code, really important, you don't need to write any explanation"),
                userMessage(REFACTORING_PROMPT_TEMPLATE.format(Map.of("smellyCode", smellyCode,
                        "requirements", requirements)))
        );
        model.chat(messages, modelResponseHandler);
    }
}
