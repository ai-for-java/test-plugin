package com.example.testplugin.testcases;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestCasesWithGpt4Action extends GenerateTestCasesAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}