package dev.ai4j.aid2.impl;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static dev.ai4j.aid2.ApiKeys.OPENAI_API_KEY;
import static dev.ai4j.chat.SystemMessage.systemMessage;
import static dev.ai4j.chat.UserMessage.userMessage;

public class AiImplementationGenerator {

    private static final PromptTemplate CREATE_IMPL_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "Write a correct, efficient and easy-readable implementation of {{impl_class_name}} class " +
                    "according to the following specification delimited by triple angle brackets <<<{{spec}}>>>. " +
                    "It is very important that you provide only working code without any comments or explanations!!! " +
                    "It is very harmful if you provide explanations or comments, so please provide only working java code!!! " +
                    "Your output should be correct and compiling java code. " +
                    "It is very important that the implementation satisfies the following test cases delimited by triple square brackets [[[{{test_class_contents}}]]]."
    );

    private final OpenAiChatModel model;

    public AiImplementationGenerator(String modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(OPENAI_API_KEY)
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void generateImplementationClassContents(String spec, String testClassContents, String implClassName, StreamingResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
                systemMessage("You are a professional Java coder."),
                userMessage(CREATE_IMPL_CLASS_PROMPT_TEMPLATE.format(Map.of(
                        "impl_class_name", implClassName,
                        "spec", spec,
                        "test_class_contents", Matcher.quoteReplacement(testClassContents)
                )))
        );

        model.chat(messages, modelResponseHandler);
    }
}
