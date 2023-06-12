package dev.ai4j.aid2.tests;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestsWithGpt4Action extends GenerateTestsAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
