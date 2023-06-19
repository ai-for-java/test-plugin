package dev.ai4j.aid2.suggestimprovements;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Config;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.chat.UserMessage;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiImprovementsSuggester {

    public void suggestImprovements(String code, StreamingResponseHandler handler) {
        PromptTemplate template = PromptTemplate.from(Config.suggestImprovementsPromptTemplate());

        UserMessage message = userMessage(template.format("code", code));

        Conversation.reset();
        Conversation.fromUser(message, handler);
    }
}
