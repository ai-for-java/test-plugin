package dev.ai4j.aid2.refactoring;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class CodeRefactoringWithGpt3Action extends CodeRefactoringAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}