package dev.ai4j.aid2.explain.summary;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.aid2.ApiKeys.OPENAI_API_KEY;
import static dev.ai4j.chat.SystemMessage.systemMessage;
import static dev.ai4j.chat.UserMessage.userMessage;

public class AiCodeSummarizer {

    private static final PromptTemplate CODE_COMMENTING_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    Explain and summarize the following code:
                                       
                    Code:
                    {{code}}
                                       
                    Guidelines:
                      - Use block comments for method-level and class-level documentation;
                      - Use inline comments to explain specific code segments;
                      - Begin comments with a capital letter and use proper grammar and punctuation;
                      - Be concise and to the point. Avoid unnecessary comments that simply restate the code;
                      - Explain the purpose of the code, not the code itself. Comments should focus on providing additional context or clarifying complex logic;
                      - Use comments to document important decisions, assumptions, or constraints related to the method or its behavior;
                      - Comment on tricky or non-intuitive parts of the code, including any workarounds or optimizations.
                     
                     Do not provide imports.
                     Do NOT provide body (implementation code) of constructors and methods, replace it with "...".
                     Provide only java code covered with comments.
                     Do NOT provide anything else!
                     """);
    private final OpenAiChatModel model;

    public AiCodeSummarizer(String modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(OPENAI_API_KEY)
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void coverWithComments(String code, StreamingResponseHandler handler) {
        List<ChatMessage> messages = List.of(
                systemMessage("You are a senior Java software engineer that explains and comments the code well."), // TODO needed?
                userMessage(CODE_COMMENTING_PROMPT_TEMPLATE.format(Map.of("code", code)))
        );
        model.chat(messages, handler);
    }
}
