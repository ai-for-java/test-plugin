package dev.ai4j.aid2.findbugs;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class FindBugsWithGpt3Action extends FindBugsAction {

    @Override
    protected String getModelName() {
        return GPT_3_5_TURBO;
    }
}
