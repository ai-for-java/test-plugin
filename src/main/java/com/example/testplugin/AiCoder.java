package com.example.testplugin;

import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.prompt.PromptTemplate;
import lombok.val;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class AiCoder {

    private static final PromptTemplate CREATE_IMPL_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "Write a correct and efficient implementation of ${impl_class_name} class according to the following specification delimited by triple angle brackets <<<${spec}>>>." +
                    "Make sure the following test cases delimited by triple square brackets pass [[[${test_class_contents}]]]." +
                    "Do not provide additional explanations or comments." +
                    "Your output should be correct and compiling java code.");

    private final OpenAiChatModel coderModel = OpenAiChatModel.builder()
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
                        "test_class_contents", testClassContents
                )).getPromptText())
        );

        return coderModel.chat(messages).getContents();
    }
}
