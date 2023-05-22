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
                        
            Provide Junit5 test method for each test case.
                        
            DO NOT provide package, imports and class signature!!!
            DO NOT provide any comments and explanations aside from test methods!!!
            Use AssertJ for assertions.
            Try to merge similar test methods into one using Junit5 @ParameterizedTest.
            Follow this structure for each test: // given, // when, // then
            Use the following structure for test method names: given_[starting conditions]__when_[action]__then_[expected result].
            DO NOT provide package, imports and class signature!!!
            DO NOT provide anything aside from test methods. DO NOT provide comments or explanations.
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
                        "class_member_contents", classMember.contents()
                )))
        );

        model.chat(messages, handler);
    }
}
