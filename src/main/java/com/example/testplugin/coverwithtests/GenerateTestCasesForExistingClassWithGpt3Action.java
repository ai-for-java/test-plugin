package com.example.testplugin.coverwithtests;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestCasesForExistingClassWithGpt3Action extends GenerateTestCasesForExistingClassAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
