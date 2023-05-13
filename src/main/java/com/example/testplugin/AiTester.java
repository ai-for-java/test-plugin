package com.example.testplugin;

import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class AiTester {

    private static final PromptTemplate CREATE_TEST_CASES_PROMPT_TEMPLATE = PromptTemplate.from(
            "Create an exhaustive list of test cases for ${impl_class_name} class to ensure correctness of it's implementation," +
                    "taking into account the following technical specification for ${impl_class_name} class delimited by triple angle brackets: <<<${spec}>>>\n" +
                    "Make sure all requirements from specification are taken into account.\n" +
                    "Consider all possible positive cases, negative cases, corner cases, edge cases, etc.\n" +
                    "For each test case, provide: 1. short description 2. inputs to the test 3. action to perform during test 4. expected outcome\n" +
                    "Limit your output to 7 most important test cases." + // TODO !!!!
                    "Do not provide any additional commentaries, provide just a numbered list of test cases."
    );

    private static final PromptTemplate CREATE_TEST_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "Given the following specification delimited by triple angle brackets <<<${spec}>>> and the following test cases delimited by triple square brackets [[[${test_cases}]]], create a ${test_class_name} class with Junit5 & AssertJ tests for each test case.\n" +
                    "Provide only a valid java code, do not provide explanations and comments.\n" +
                    "Tests should be easy to read and understand.\n" +
                    "Follow this structure for each test: // given, // when, // then.\n" +
                    "Use the following structure for test method names: given_[starting conditions]__when_[action]__then_[expected result]"
    );

    private final OpenAiChatModel testerModel = OpenAiChatModel.builder()
            .modelName(GPT_3_5_TURBO)
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .temperature(0.0)
            .timeout(Duration.ofMinutes(10))
            .build();

    public String generateTestCases(String spec, String implClassName) {
        List<ChatMessage> messages = List.of(
                messageFromSystem("You are a professional software tester."),
                messageFromHuman(CREATE_TEST_CASES_PROMPT_TEMPLATE.apply(Map.of(
                        "impl_class_name", implClassName,
                        "spec", spec
                )).getPromptText())
        );

        return testerModel.chat(messages).getContents();
    }

    public String generateTestClassContents(String spec, String testCases, String testClassName) {
        List<ChatMessage> messages = List.of(
                messageFromSystem("You are a professional software tester."),
                messageFromHuman(CREATE_TEST_CLASS_PROMPT_TEMPLATE.apply(Map.of(
                        "spec", spec,
                        "test_cases", testCases,
                        "test_class_name", testClassName
                )).getPromptText())
        );

        return testerModel.chat(messages).getContents();
    }
}
