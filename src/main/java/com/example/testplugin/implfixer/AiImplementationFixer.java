package com.example.testplugin.implfixer;

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

public class AiImplementationFixer {

    private static final PromptTemplate FIX_IMPL_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "The following tests delimited by triple angle brackets <<<${test_class_contents}>>> fail with the following errors delimited by triple square brackets [[[${console_output}]]].\n" +
                    "Provide a fixed and working version of the following class (which is delimited by triple parentheses) (((${impl_class_contents}))).\n" +
                    "It is very important that you provide only working code without any comments or explanations!!!\n" +
                    "It is very harmful if you provide explanations or comments, so please provide only working java code!!!"
    );

    private final OpenAiChatModel model;

    public AiImplementationFixer(OpenAiModelName modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void fix(String testClassContents, String consoleOutput, String implClassContents, ModelResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
                messageFromHuman(FIX_IMPL_CLASS_PROMPT_TEMPLATE.apply(Map.of(
                        "test_class_contents", Matcher.quoteReplacement(testClassContents),
                        "console_output", Matcher.quoteReplacement(consoleOutput),
                        "impl_class_contents", Matcher.quoteReplacement(implClassContents)
                )).getPromptText())
        );

        model.chat(messages, modelResponseHandler);
    }
}
