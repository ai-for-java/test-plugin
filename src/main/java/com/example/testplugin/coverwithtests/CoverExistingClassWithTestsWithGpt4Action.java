package com.example.testplugin.coverwithtests;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CoverExistingClassWithTestsWithGpt4Action extends CoverExistingClassWithTestsAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
