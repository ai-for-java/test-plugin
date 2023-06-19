package dev.ai4j.aid2;

import com.intellij.ide.util.PropertiesComponent;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Config {

    private static final String AID2_OPENAI_API_KEY = "AID2_OPENAI_API_KEY";
    private static final String AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE = "AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE";
    private static final String AID2_EXPLAIN_CODE_PROMPT_TEMPLATE = "AID2_EXPLAIN_CODE_PROMPT_TEMPLATE";
    private static final String AID2_FIND_BUGS_PROMPT_TEMPLATE = "AID2_FIND_BUGS_PROMPT_TEMPLATE";

    // OpenAI Key

    public static String openAiApiKey() {
        String key = PropertiesComponent.getInstance().getValue(AID2_OPENAI_API_KEY);
        return isBlank(key) ? "" : key;
    }

    public static void openAiApiKey(String value) {
        PropertiesComponent.getInstance().setValue(AID2_OPENAI_API_KEY, value);
    }

    // cover with comments

    public static String coverWithCommentsPromptTemplate() {
        String template = PropertiesComponent.getInstance().getValue(AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE);
        return isBlank(template) ? DEFAULT_COVER_WITH_COMMENTS_PROMPT_TEMPLATE : template;
    }

    private static final String DEFAULT_COVER_WITH_COMMENTS_PROMPT_TEMPLATE = """
            Cover the following code with comments:

            Code:
            {{code}}

            Guidelines:
            Be concise and to the point. Avoid unnecessary comments that simply restate the code.
            Explain the purpose of the code, not the code itself.
            Comments should focus on providing additional context or clarifying complex logic.
            Comment on tricky or non-intuitive parts of the code, including any workarounds or optimizations.
            Do not print package declaration and imports.
            DO NOT Output body (implementation code) of constructors and methods, replace it with "...".
            Provide a short 3-sentence summary of the class functionality right over the class definition (as a javadoc).
            For each class member (field, constructor, method, etc) provide a 2-3 sentence summary of what it does and how.
            Provide only java code covered with comments, nothing else.
            """;

    public static void coverWithCommentsPromptTemplate(String value) {
        PropertiesComponent.getInstance().setValue(AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE, value);
    }

    // explain code

    public static String explainCodePromptTemplate() {
        String template = PropertiesComponent.getInstance().getValue(AID2_EXPLAIN_CODE_PROMPT_TEMPLATE);
        return isBlank(template) ? DEFAULT_EXPLAIN_CODE_PROMPT_TEMPLATE : template;
    }

    private static final String DEFAULT_EXPLAIN_CODE_PROMPT_TEMPLATE = """
            I have a piece of code that I'm trying to understand.
            I'm seeing it for the first time.
            Can you help me analyze this code?
            I'm particularly interested in:

            - The overall purpose and functionality of the class.
            - The key methods or functions in the class, including their responsibilities, inputs, outputs, and side effects.
            - The properties or fields of the class, including their types and when and how they are modified.
            - Any dependencies the class has on other classes or external libraries.

            Here is the code:
            {{code}}

            Provide your answer as a list of bullet points for each of 4 categories mentioned above.
            Each bullet point should be concise (max 1 sentence) and easy to understand.
            """;

    public static void explainCodePromptTemplate(String value) {
        PropertiesComponent.getInstance().setValue(AID2_EXPLAIN_CODE_PROMPT_TEMPLATE, value);
    }

    // find bugs

    public static String findBugsPromptTemplate() {
        String template = PropertiesComponent.getInstance().getValue(AID2_FIND_BUGS_PROMPT_TEMPLATE);
        return isBlank(template) ? DEFAULT_FIND_BUGS_PROMPT_TEMPLATE : template;
    }

    private static final String DEFAULT_FIND_BUGS_PROMPT_TEMPLATE = """
            I have a piece of code that I'm examining.
            I strictly need you to identify any existing bugs that would prevent this code from functioning as intended.
            Please don't consider any improvements, refactorings, or design suggestions.
            I am solely focused on bugs that would cause incorrect results, unhandled exceptions, crashes, or any similar issues that would disrupt the execution of the code.

            Here is the code:
            {{code}}
            """;

    public static void findBugsPromptTemplate(String value) {
        PropertiesComponent.getInstance().setValue(AID2_FIND_BUGS_PROMPT_TEMPLATE, value);
    }
}
