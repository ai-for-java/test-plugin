package com.example.testplugin.coverwithtests;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class GenerateTestsFromTestCasesWithGpt3Action extends GenerateTestsFromTestCasesAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_3_5_TURBO;
    }
}
