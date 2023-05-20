package com.example.testplugin.coverwithtests;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;

public class AiTestGenerator {

    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            Given the following class:
            {{class_contents}}
                        
            And the following list of test cases:
            {{test_cases}}
                        
            For each test case, provide Junit5 test method for the following {{class_member_type}}:
            {{class_member_contents}}
                        
            Each test case should have the following structure:
            {{test_case_structure}}
                        
            Remember that in java you CANNOT use "null" as a value for primitive types (byte, short, int, long, float, double, boolean, char)!!!
            Remember that you DO NOT have access to non-public class fields, so you CANNOT verify and assert them after the test!!!
            DO NOT provide code that cannot be compiled!!!
            Do not provide any comments and explanations aside from test methods.
            Each test method should be separated by a single blank line.
            Use AssertJ for assertions.
            """
    );

    private final OpenAiChatModel model;

    public AiTestGenerator(OpenAiModelName modelName) {
        this.model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();
    }

    public void generateTestsFor(String classContents, String testCases, ClassMember classMember, ModelResponseHandler handler) {
        List<ChatMessage> messages = List.of(
                messageFromHuman(PROMPT_TEMPLATE.with(Map.of(
                        "class_contents", classContents,
                        "test_cases", testCases,
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
                    Constructor input: (here you define test inputs for the constructor)
                    Result: (here you define whether exception is expected to be thrown. if yes, then define the type of exception and precise error message)
                    """;
            case METHOD -> """
                    Given: (here you use constructor / builder / static factory method in order to build an object in a state needed for the test. do not use test inputs that cause exception during object construction. you also prepare all test data needed to call the method)
                    When: (here you call the method)
                    Then: (here you assert output of the method and verify all things that can be verified in order to make sure that method works as expected. you CANNOT use fields of a class for verification, only method output and non-private methods!)
                    """;
        };
    }
}
