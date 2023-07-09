package dev.ai4j.aid2.tests;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.chat.UserMessage;

import java.util.Map;
import java.util.regex.Matcher;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiTestGenerator {

    private static final PromptTemplate CREATE_TEST_CLASS_PROMPT_TEMPLATE = PromptTemplate.from(
            "Given the following specification delimited by triple angle brackets <<<{{spec}}>>> and the following test cases delimited by triple square brackets [[[{{test_cases}}]]], create a {{test_class_name}} class with Junit5 & AssertJ tests for each test case.\n" +
                    "Provide only a valid java code, do not provide explanations and comments.\n" +
                    "Tests should be easy to read and understand.\n" +
                    "Follow this structure for each test: // given, // when, // then.\n" +
                    "Use the following structure for test method names: given_[starting conditions]__when_[action]__then_[expected result]"
    );

    public void generateTestClassContents(String spec, String testCases, String testClassName, StreamingResponseHandler modelResponseHandler) {
        UserMessage message = userMessage(CREATE_TEST_CLASS_PROMPT_TEMPLATE.format(Map.of(
                "spec", Matcher.quoteReplacement(spec),
                "test_cases", Matcher.quoteReplacement(testCases), // TODO move to ai4j?
                "test_class_name", testClassName
        )));

        Conversation.reset();
        Conversation.fromUser(message, modelResponseHandler);
    }
}
