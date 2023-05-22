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
                        
            Think about what is the best strategy to test the following {{class_member_type}}:
            {{class_member_contents}}
                        
            Make sure you cover all execution paths on this method and consider all the possible negative, positive, edge and corner cases.
            Take into consideration that you cannot verify class fields directly, so the only way you can verify the method behaves as expected is to verify method output and use other non-private methods (if needed).
            Make sure you test behaviour, not implementation details.
            Do not provide code yet, just provide your thoughts.
            Let's think step by step."""
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
