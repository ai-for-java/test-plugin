package dev.ai4j.aid2.spec;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.chat.UserMessage;

import java.util.Map;
import java.util.regex.Matcher;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiSpecAssesser {

    private static final PromptTemplate ASSESS_SPEC_PROMPT_TEMPLATE = PromptTemplate.from(
            "Provide a list (ordered from most critical to least critical) of issues such as incomplete, contradictory and ambiguous requirements in the following technical specification delimited by triple angle brackets: <<<{{spec}}>>>\n" +
                    "After mentioning all the problems with existing specification, provide an example of comprehensive specification in a form of a list with detailed technical requirements.");

    public void assessSpecification(String spec, StreamingResponseHandler modelResponseHandler) {
        UserMessage message = userMessage(ASSESS_SPEC_PROMPT_TEMPLATE.format(Map.of(
                "spec", Matcher.quoteReplacement(spec)
        )));

        Conversation.reset();
        Conversation.fromUser(message, modelResponseHandler);
    }
}
