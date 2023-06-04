package com.example.testplugin.codeexplanation;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class CodeCommentingWithGpt3Action extends CodeCommentingAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
