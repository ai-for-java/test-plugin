package com.example.testplugin.refactoring;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeRefactoringWithGpt4Action extends CodeRefactoringAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
