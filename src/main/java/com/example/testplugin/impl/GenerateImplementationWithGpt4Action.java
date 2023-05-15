package com.example.testplugin.impl;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateImplementationWithGpt4Action extends GenerateImplementationAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
