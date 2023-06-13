package dev.ai4j.aid2.coverwithtests;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class GenerateTestCasesForExistingClassWithGpt4Action extends GenerateTestCasesForExistingClassAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}