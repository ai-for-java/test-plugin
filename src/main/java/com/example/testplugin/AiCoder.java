package com.example.testplugin;

import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.MessageFromAi;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class AiCoder {


    OpenAiChatModel testerModel = OpenAiChatModel.builder()
            .modelName(GPT_3_5_TURBO) // TODO try 4
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .temperature(0.0)
            .timeout(Duration.ofSeconds(2 * 60))
            .build();
    String testerSystemPrompt = "You are a professional software tester.\n" +
            "You write unit tests in Junit5 and AssertJ according to a provided specification.\n" +
            "You write as many unit tests as possible to cover all possible positive, negative and corner cases.\n" +
            "You use a meaningful and clear naming for each test case (method).\n" +
            "Separate each test with double newline into 3 parts: 1. \"given\" 2. \"when\" 3. \"then\".\n" +
            "Do not provide any comments and do not explain the code.\n" +
            "Just output the whole contents of a java file." +
            "Your output should be valida for compilation." +
            "Make sure you provide all necessary imports!";


    OpenAiChatModel coderModel = OpenAiChatModel.builder()
            .modelName(GPT_3_5_TURBO)
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .temperature(0.0)
            .timeout(Duration.ofSeconds(120))
            .build();
    String coderSystemPrompt = "You are a professional Java coder.\n" +
            "You provide an implementation of a class according to a provided specification.\n" +
            "You take into account provided junit test cases and make sure that generated code is passing those tests.\n" +
            "Do not provide any comments and do not explain the code.\n" +
            "Just output the whole contents of a java file.";

    public String generateTestClassContents(String spec, String testClassName) {

        List<ChatMessage> messages = List.of(
                messageFromSystem(testerSystemPrompt),
                messageFromHuman(String.format("Test class name should be '%s'." +
                        "Avoid package name in the java file." +
                        "Avoid triple quotes, no need to format the code." +
                        "Do not write anything that cannot be compiled. Provide only code without any comments." +
                        "Here is the specification: %s", testClassName, spec))
        );

        MessageFromAi fromAi = testerModel.chat(messages);

        return fromAi.getContents();
    }

    public String generateImplementationClassContents(String spec, String testClassContents) {
        List<ChatMessage> messages = List.of(
                messageFromSystem(coderSystemPrompt),
                messageFromHuman(String.format(
                        "Here is the specification, delimited by triple angle brackets: <<<%s>>>.\n" +
                                "Here are tests for your implementation, delimited by triple square brackets: [[[%s]]].",
                        spec, testClassContents))
        );

        MessageFromAi fromAi = coderModel.chat(messages);

        return fromAi.getContents();
    }
}
