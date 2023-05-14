package com.example.testplugin.spec;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class AssessSpecWithGpt3Action extends AssessSpecAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_3_5_TURBO;
    }
}
