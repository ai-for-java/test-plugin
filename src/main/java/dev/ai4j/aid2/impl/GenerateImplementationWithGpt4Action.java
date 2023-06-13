package dev.ai4j.aid2.impl;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateImplementationWithGpt4Action extends GenerateImplementationAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}