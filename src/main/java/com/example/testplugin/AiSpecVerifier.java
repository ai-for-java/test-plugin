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

public class AiSpecVerifier {

    private static final PromptTemplate VERIFY_SPEC_PROMPT_TEMPLATE = PromptTemplate.from(
            "Provide a list (ordered from most critical to least critical) of at least 10 issues (incomplete, contradictory and ambiguous requirements) in the following technical specification delimited by triple angle brackets (in the context of Java): <<<${spec}>>>");

    private final OpenAiChatModel specVerifierModel = OpenAiChatModel.builder()
            .modelName(GPT_3_5_TURBO) // TODO try 4?
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .temperature(0.0)
            .timeout(Duration.ofMinutes(10))
            .build();

    public String verifySpecification(String spec) {
        List<ChatMessage> messages = List.of(
                messageFromSystem("You are a professional java coder."),
                messageFromHuman(VERIFY_SPEC_PROMPT_TEMPLATE.apply(Map.of(
                        "spec", spec
                )).getPromptText())
        );

        return specVerifierModel.chat(messages).getContents();

    }
}
