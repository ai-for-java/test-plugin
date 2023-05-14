package com.example.testplugin.testcases;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;

public class AiTestCaseGenerator {

    private static final PromptTemplate CREATE_TEST_CASES_PROMPT_TEMPLATE = PromptTemplate.from(
            "Create an exhaustive list of test cases for ${impl_class_name} java class according to " +
                    "the following technical specification delimited by triple angle brackets: <<<${spec}>>>.\n" +
                    "Each test case should be very detailed, specific and follow the following BDD-style structure:\n" +
                    "(easy to read and understand description of a test case)\n" +
                    "Given: (starting conditions for a test and examples of inputs)\n" +
                    "When: (action that should be performed during the test)\n" +
                    "Then: (expected outcome of the test with examples)\n" +
                    "\n" +
                    "Consider all possible positive cases, negative cases, corner cases, edge cases, etc.\n" +
                    "It is very important that each detail and requirement in specification is taken into account."
    );

    private final OpenAiChatModel model;

    public AiTestCaseGenerator(OpenAiModelName modelName) {
        model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public String generateTestCases(String spec, String implClassName) {
        List<ChatMessage> messages = List.of(
                messageFromHuman(CREATE_TEST_CASES_PROMPT_TEMPLATE.apply(Map.of(
                        "impl_class_name", implClassName,
                        "spec", spec
                )).getPromptText())
        );

        return model.chat(messages).getContents();
    }

    public void generateTestCases(String spec, String implClassName, ModelResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
                messageFromHuman(CREATE_TEST_CASES_PROMPT_TEMPLATE.apply(Map.of(
                        "impl_class_name", implClassName,
                        "spec", spec
                )).getPromptText())
        );

        model.chat(messages, modelResponseHandler);
    }
}
