package dev.ai4j.aid2.impl;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class GenerateImplementationWithGpt3Action extends GenerateImplementationAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
