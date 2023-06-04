package com.example.testplugin.testcases;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class GenerateTestCasesWithGpt3Action extends GenerateTestCasesAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}