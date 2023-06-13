package dev.ai4j.aid2;

import com.intellij.ide.util.PropertiesComponent;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Config {

    private static final String AID2_OPENAI_API_KEY = "AID2_OPENAI_API_KEY";
    private static final String AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE = "AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE";

    public static String openAiApiKey() {
        return PropertiesComponent.getInstance().getValue(AID2_OPENAI_API_KEY);
    }

    public static void openAiApiKey(String value) {
        PropertiesComponent.getInstance().setValue(AID2_OPENAI_API_KEY, value);
    }

    public static String coverWithCommentsPromptTemplate() {
        String template = PropertiesComponent.getInstance().getValue(AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE);
        return isBlank(template) ? DEFAULT_COVER_WITH_COMMENTS_PROMPT_TEMPLATE : template;
    }

    private static final String DEFAULT_COVER_WITH_COMMENTS_PROMPT_TEMPLATE = """
            Cover the following code with comments:
                               
            Code:
            {{code}}
                               
            Guidelines:
            Use block comments for method-level and class-level documentation;
            Use inline comments to explain specific code segments;
            Begin comments with a capital letter and use proper grammar and punctuation;
            Be concise and to the point. Avoid unnecessary comments that simply restate the code;
            Explain the purpose of the code, not the code itself. Comments should focus on providing additional context or clarifying complex logic;
            Use comments to document important decisions, assumptions, or constraints related to the method or its behavior;
            Comment on tricky or non-intuitive parts of the code, including any workarounds or optimizations.
            Do not provide imports.
            Do NOT provide body (implementation code) of constructors and methods, replace it with "...".
            Provide only java code covered with comments.
            Do NOT provide anything else!
            """;

    public static void coverWithCommentsPromptTemplate(String value) {
        PropertiesComponent.getInstance().setValue(AID2_COVER_WITH_COMMENTS_PROMPT_TEMPLATE, value);
    }
}
