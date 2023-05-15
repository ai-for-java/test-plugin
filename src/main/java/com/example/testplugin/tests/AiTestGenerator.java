package com.example.testplugin.tests;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;

public class AiTestGenerator {

    private static final PromptTemplate CREATE_TEST_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "Given the following specification delimited by triple angle brackets <<<${spec}>>> and the following test cases delimited by triple square brackets [[[${test_cases}]]], create a ${test_class_name} class with Junit5 & AssertJ tests for each test case.\n" +
                    "Provide only a valid java code, do not provide explanations and comments.\n" +
                    "Tests should be easy to read and understand.\n" +
                    "Follow this structure for each test: // given, // when, // then.\n" +
                    "Use the following structure for test method names: given_[starting conditions]__when_[action]__then_[expected result]"
    );

    private final OpenAiChatModel model;

    public AiTestGenerator(OpenAiModelName modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void generateTestClassContents(String spec, String testCases, String testClassName, ModelResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
                messageFromSystem("You are a professional software tester."), // TODO try without?
                messageFromHuman(CREATE_TEST_CLASS_PROMPT_TEMPLATE.apply(Map.of(
                        "spec", spec,
                        "test_cases", Matcher.quoteReplacement(testCases), // TODO move to ai4j?
                        "test_class_name", testClassName
                )).getPromptText())
        );

        model.chat(messages, modelResponseHandler);
    }
}
