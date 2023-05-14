package com.example.testplugin.testcases;

import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class AiTestCaseGenerator {

    private static final PromptTemplate CREATE_TEST_CASES_PROMPT_TEMPLATE = PromptTemplate.from(
            "Create an exhaustive list of test cases for ${impl_class_name} class " +
                    "according to the following technical specification delimited by triple angle brackets: <<<${spec}>>>\n" +
                    "Each test case should be very detailed, specific and follow the following structure delimited by triple square brackets:[[[\n" +
                    "1. Test case description (easy to understand description of test case)\n" +
                    "2. Given (starting conditions for a test with examples of inputs)\n" +
                    "3. When (action that should be performed during the test)\n" +
                    "4. Then (expected outcome of the test with examples)\n" +
                    "]]]\n" +
                    "It is very important that each detail and requirement from specification is taken into account.\n" +
                    "Consider all possible positive cases, negative cases, corner cases, edge cases, etc."
    );

    private final OpenAiChatModel model = OpenAiChatModel.builder()
            .modelName(GPT_4) // TODO try 4
            .apiKeys(List.of(
                    System.getenv("OPENAI_API_KEY_2"),
                    System.getenv("OPENAI_API_KEY")
            ))
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

        return model.chat(messages).getContents();
    }
}
