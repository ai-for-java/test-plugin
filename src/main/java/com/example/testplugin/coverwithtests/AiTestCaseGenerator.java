package com.example.testplugin.coverwithtests;

import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.example.testplugin.coverwithtests.ClassMember.ClassMemberType.CONSTRUCTOR;
import static com.example.testplugin.coverwithtests.ClassMember.ClassMemberType.METHOD;
import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static java.util.Arrays.stream;

public class AiTestCaseGenerator {

    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from("""
            Given the following class:
            {{class_contents}}
                        
            Provide an exhaustive list of test cases for the following {{class_member_type}}:
            {{class_member_contents}}
                        
            Each test case should have the following structure:
            {{test_case_structure}}
                        
            Remember that in java you CANNOT use "null" as a value for primitive types (byte, short, int, long, float, double, boolean, char)!!!
            Remember that you DO NOT have access to non-public class fields, so you CANNOT verify and assert them after the test!!!
            DO NOT provide test  cases that cannot be compiled!!!
            Do not provide any comments and explanations aside from test cases.
            Each test case block should be separated by a single blank line.
            It is very important that you provide all the possible positive, negative, corner and edge cases/scenarios needed to verify the correct behaviour!!!
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

    public String generateTestCasesFor(String classContents, ClassMember classMember) {
        List<ChatMessage> messages = List.of(
                messageFromHuman(PROMPT_TEMPLATE.apply(Map.of(
                        "class_contents", classContents,
                        "class_member_type", classMember.type().toString().toLowerCase(),
                        "class_member_contents", classMember.contents(),
                        "test_case_structure", getTestCaseStructureFor(classMember.type())
                )).getPromptText())
        );

        return model.chat(messages).getContents();
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
