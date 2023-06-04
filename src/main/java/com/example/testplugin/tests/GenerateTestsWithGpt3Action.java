package com.example.testplugin.tests;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class GenerateTestsWithGpt3Action extends GenerateTestsAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
