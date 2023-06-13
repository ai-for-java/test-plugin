package dev.ai4j.aid2.explain.comment;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeCommentingWithGpt4Action extends CodeCommentingAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}