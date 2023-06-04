package com.example.testplugin.coverwithtests;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestsFromTestCasesWithGpt4Action extends GenerateTestsFromTestCasesAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
