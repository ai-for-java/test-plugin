package dev.ai4j.aid2.explain.summary;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class CodeSummarizingWithGpt3Action extends CodeSummarizingAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
