package dev.ai4j.aid2.codereadabilityanalyzer;

import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Config;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static dev.ai4j.chat.SystemMessage.systemMessage;
import static dev.ai4j.chat.UserMessage.userMessage;

public class AiCodeReadabilityAnalyzer {

    private static final PromptTemplate CODE_READABILITY_ANALYZER_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    When it comes to refactoring Java code for improved readability, there are several concepts and techniques you can apply. Here are some suggestions: - Naming conventions: Use meaningful and descriptive names for variables, methods, and classes. Avoid cryptic or abbreviated names that make it hard to understand the purpose or functionality.
                     - Single Responsibility Principle (SRP): Ensure that each class and method has a single responsibility. Split large methods into smaller ones, each performing a specific task. This improves readability and makes the code easier to understand and maintain.
                     - Remove duplication: Identify repetitive code blocks and extract them into reusable methods or utility classes. Duplicated code can be hard to read and maintain, so consolidating it improves readability and reduces the chance of errors.
                     - Comments and documentation: Add clear and concise comments to explain complex or non-obvious code segments. However, strive to write self-explanatory code that doesn't rely heavily on comments. Additionally, consider generating code documentation using tools like Javadoc.
                     - Formatting and indentation: Apply consistent and clear formatting to your code. Use proper indentation, spacing, and line breaks to enhance readability. IDEs like Eclipse or IntelliJ IDEA have built-in formatting tools that can automatically apply formatting rules.
                     - Use meaningful exception handling: Avoid generic exception handling where possible. Catch specific exceptions and handle them appropriately. This provides better clarity about the potential errors and improves code comprehension.
                     - Avoid deep nesting: Reduce excessive levels of nesting in conditional statements (if-else, switch-case, etc.). Refactor complex conditions and extract them into separate methods or variables with descriptive names.
                     - Use meaningful abstractions: Identify opportunities to create higher-level abstractions to encapsulate complex logic. This can involve creating classes, interfaces, or utility methods to provide a more expressive and readable API.
                     - Follow coding style guidelines: Adhere to established coding style guidelines such as the Java Code Conventions or your organization's coding standards. Consistency in coding style across a project improves readability for everyone working on it.
                    Please provide a list of bullet points about what should be changed to improve the readability of existing code.Make sure you don't!!! advise changing public API.Provide code snippets for each change to this {{smellyCode}}:""");

    private final String modelName;

    public AiCodeReadabilityAnalyzer(String modelName) {
        this.modelName = modelName;
    }

    public void analyzeReadabilityOfCode(String smellyCode, StreamingResponseHandler handler) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .modelName(modelName)
                .apiKey(Config.openAiApiKey())
                .temperature(0.0)
                .timeout(Duration.ofMinutes(10))
                .build();

        List<ChatMessage> messages = List.of(
                systemMessage("you are a senior Java software engineer that refactors Java code very well. You provide only clean code, really important, you don't need to write any explanation"),
                userMessage(CODE_READABILITY_ANALYZER_PROMPT_TEMPLATE.format(Map.of("smellyCode", smellyCode)))
        );

        model.chat(messages, handler);
    }
}
