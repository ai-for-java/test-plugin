package com.example.testplugin.testcases;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestCasesWithGpt4Action extends GenerateTestCasesAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}