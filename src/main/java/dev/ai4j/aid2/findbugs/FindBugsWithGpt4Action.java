package dev.ai4j.aid2.findbugs;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class FindBugsWithGpt4Action extends FindBugsAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
