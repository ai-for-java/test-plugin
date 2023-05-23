package com.example.testplugin.codeexplanation;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeCommentingWithGpt4Action extends CodeCommentingAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
