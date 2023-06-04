package com.example.testplugin.refactoring;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeRefactoringWithGpt4Action extends CodeRefactoringAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
