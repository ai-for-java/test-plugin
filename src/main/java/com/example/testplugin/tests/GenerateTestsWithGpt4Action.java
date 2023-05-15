package com.example.testplugin.tests;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestsWithGpt4Action extends GenerateTestsAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
