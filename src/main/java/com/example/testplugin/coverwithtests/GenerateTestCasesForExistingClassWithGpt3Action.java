package com.example.testplugin.coverwithtests;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestCasesForExistingClassWithGpt3Action extends GenerateTestCasesForExistingClassAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
