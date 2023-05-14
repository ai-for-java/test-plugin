package com.example.testplugin.impl;

import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class AiImplementationGenerator {

    private static final PromptTemplate CREATE_IMPL_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "Write a correct, efficient and easy-readable implementation of ${impl_class_name} class " +
                    "according to the following specification delimited by triple angle brackets <<<${spec}>>>. " +
                    "Do not provide additional explanations or comments. " +
                    "Your output should be correct and compiling java code. " +
                    "It is very important that the implementation satisfies the following test cases delimited by triple square brackets [[[${test_class_contents}]]]."
    );

    private final OpenAiChatModel model = OpenAiChatModel.builder()
            .modelName(GPT_3_5_TURBO)
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .temperature(0.0)
            .timeout(Duration.ofMinutes(10))
            .build();

    public String generateImplementationClassContents(String spec, String testClassContents, String implClassName) {
        List<ChatMessage> messages = List.of(
                messageFromSystem("You are a professional Java coder."),
                messageFromHuman(CREATE_IMPL_CLASS_PROMPT_TEMPLATE.apply(Map.of(
                        "impl_class_name", implClassName,
                        "spec", spec,
                        "test_class_contents", Matcher.quoteReplacement(testClassContents)
                )).getPromptText())
        );

        return model.chat(messages).getContents();
    }
}
