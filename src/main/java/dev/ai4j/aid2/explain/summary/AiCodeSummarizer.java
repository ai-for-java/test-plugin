package dev.ai4j.aid2.explain.summary;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Config;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.chat.UserMessage;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiCodeSummarizer {

    public void coverWithComments(String code, StreamingResponseHandler handler) {
        PromptTemplate template = PromptTemplate.from(Config.explainCodePromptTemplate());

        UserMessage message = userMessage(template.format("code", code));

        Conversation.reset();
        Conversation.fromUser(message, handler);
    }
}
