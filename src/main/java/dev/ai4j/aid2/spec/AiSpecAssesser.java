package dev.ai4j.aid2.spec;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.aid2.ApiKeys.OPENAI_API_KEY;
import static dev.ai4j.chat.UserMessage.userMessage;

public class AiSpecAssesser {

    private static final PromptTemplate ASSESS_SPEC_PROMPT_TEMPLATE = PromptTemplate.from(
            "Provide a list (ordered from most critical to least critical) of issues such as incomplete, contradictory and ambiguous requirements in the following technical specification delimited by triple angle brackets: <<<{{spec}}>>>\n" +
                    "After mentioning all the problems with existing specification, provide an example of comprehensive specification in a form of a list with detailed technical requirements.");

    private final OpenAiChatModel model;

    public AiSpecAssesser(String modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(OPENAI_API_KEY)
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void assessSpecification(String spec, StreamingResponseHandler modelResponseHandler) {
        List<ChatMessage> messages = List.of(
//                SystemMessage("You are a professional java coder."),
                userMessage(ASSESS_SPEC_PROMPT_TEMPLATE.format(Map.of(
                        "spec", spec
                )))
        );

        model.chat(messages, modelResponseHandler);
    }
}
