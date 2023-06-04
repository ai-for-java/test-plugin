package com.example.testplugin.spec;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class AssessSpecWithGpt3Action extends AssessSpecAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
