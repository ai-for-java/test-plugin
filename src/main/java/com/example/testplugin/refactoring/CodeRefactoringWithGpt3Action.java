package com.example.testplugin.refactoring;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class CodeRefactoringWithGpt3Action extends CodeRefactoringAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_3_5_TURBO;
    }
}
