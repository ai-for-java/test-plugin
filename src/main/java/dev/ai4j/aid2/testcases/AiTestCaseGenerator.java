package dev.ai4j.aid2.testcases;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.chat.UserMessage;

import java.util.Map;
import java.util.regex.Matcher;

import static dev.ai4j.chat.UserMessage.userMessage;

public class AiTestCaseGenerator {

    private static final PromptTemplate CREATE_TEST_CASES_PROMPT_TEMPLATE = PromptTemplate.from(
            "Create an exhaustive list of test cases for {{impl_class_name}} java class according to " +
                    "the following technical specification delimited by triple angle brackets: <<<{{spec}}>>>.\n" +
                    "Each test case should be very detailed, specific and follow the following BDD-style structure:\n" +
                    "Test case #x: (put here an easy to read and understand description of a test case)\n" +
                    "Given: (starting conditions for a test with concrete examples of inputs to the test)\n" +
                    "When: (action that should be performed during the test. write detailed description,  notcode.)\n" +
                    "Then: (expected outcome of the test with concrete examples and what should be verified)\n" +
                    "\n\n" +
                    "Each test case should be separated from other by double newline.\n" +
                    "Do not group test cases in 'Positive Test Cases', 'Negative Test Cases' or similar.\n" +
                    "Consider all possible positive cases, negative cases, corner cases, edge cases, etc.\n" +
                    "It is very important that each detail and requirement in specification is taken into account."
    );

    public void generateTestCases(String spec, String implClassName, StreamingResponseHandler modelResponseHandler) {
        UserMessage message = userMessage(CREATE_TEST_CASES_PROMPT_TEMPLATE.format(Map.of(
                "impl_class_name", implClassName,
                "spec", Matcher.quoteReplacement(spec) // TODO escape
        )));

        Conversation.reset();
        Conversation.fromUser(message, modelResponseHandler);
    }
}
