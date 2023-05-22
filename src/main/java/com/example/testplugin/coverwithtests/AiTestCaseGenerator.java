package com.example.testplugin.coverwithtests;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.completion.OpenAiCompletionModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;

public class AiTestCaseGenerator {

    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            Given the following class:
            {{class_contents}}
            
            Provide an exhaustive list of Junit5 and AssertJ test cases for the following {{class_member_type}}:
            {{class_member_contents}}
            
            Each test case should have the following structure:
            {{test_case_structure}}
            
            Remember that in java you CANNOT use "null" as a value for primitive types (byte, short, int, long, float, double, boolean, char)!!!
            Remember that you DO NOT have access to non-public class fields, so you CANNOT verify and assert them after the test!!!
            DO NOT provide test  cases that cannot be compiled!!!
            Do not provide any comments and explanations aside from test cases.
            Each test case block should be separated by a single blank line.
            Use the following structure for test method names: given_[starting conditions]__when_[action]__then_[expected result].
            DO NOT provide test cases for getters and setters.
            DO NOT provide package, imports and class signature!!!
            DO NOT provide anything aside from test methods. DO NOT provide comments or explanations.
            It is very important that you provide all the possible positive, negative, corner and edge cases/scenarios needed to verify all the execution paths!!!
            """
    );

    private final OpenAiChatModel model;

    public AiTestCaseGenerator(OpenAiModelName modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void generateTestCasesFor(String classContents, ClassMember classMember, ModelResponseHandler handler) {
        List<ChatMessage> messages = List.of(
//                messageFromSystem("You are Kent Beck, master of TDD."),
                messageFromHuman(PROMPT_TEMPLATE.with(Map.of(
                        "class_contents", classContents,
                        "class_member_type", classMember.type().toString().toLowerCase(),
                        "class_member_contents", classMember.contents(),
                        "test_case_structure", getTestCaseStructureFor(classMember.type())
                )))
        );

        model.chat(messages, handler);
    }

    private static String getTestCaseStructureFor(ClassMember.ClassMemberType type) {
        return switch (type) {
            case CONSTRUCTOR -> """
                    // given (here you define test inputs for the constructor)
                    // when (here you call the constructor)
                    // then (here you define whether exception is expected to be thrown. if yes, then define the type of exception and precise error message)
                    """;
            case METHOD -> """
                    // given (here you use constructor / builder / static factory method in order to build an object in a state needed for the test. do not use test inputs that cause exception during object construction. you also prepare all test data needed to call the method)
                    // when (here you call the method)
                    // then (here you specify assertions that should be done on the output of the method (if method returns something) and verify all things that can be verified in order to make sure that method works as expected. you CANNOT use fields of a class for verification, only method output and non-private methods! Make sure you verify everything you can in order to make sure method works as expected!)
                    """;
        };
    }
}
