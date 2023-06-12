package dev.ai4j.aid2.explain.summary;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeSummarizingWithGpt4Action extends CodeSummarizingAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
