package com.example.testplugin.spec;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class AssessSpecWithGpt4Action extends AssessSpecAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
