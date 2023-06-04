package com.example.testplugin.coverwithtests;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class GenerateTestsFromTestCasesWithGpt3Action extends GenerateTestsFromTestCasesAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
