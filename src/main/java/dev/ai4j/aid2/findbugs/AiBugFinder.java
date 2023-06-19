package dev.ai4j.aid2.findbugs;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Config;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.chat.UserMessage;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiBugFinder {

    private final String modelName;

    public AiBugFinder(String modelName) {
        this.modelName = modelName;
    }

    public void findBugs(String code, StreamingResponseHandler handler) {
        PromptTemplate template = PromptTemplate.from(Config.findBugsPromptTemplate());

        UserMessage message = userMessage(template.format("code", code));

        Conversation.reset(modelName);
        Conversation.fromUser(message, handler);
    }
}
