package com.example.testplugin.codereadabilityanalyzer;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class CodeReadabilityAnalyzeWithGpt3Action extends CodeReadabilityAnalyseAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_3_5_TURBO;
    }
}
