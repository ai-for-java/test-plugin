package com.example.testplugin.spec;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class AssessSpecWithGpt4Action extends AssessSpecAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
