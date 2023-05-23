package com.example.testplugin.codereadabilityanalyzer;

import dev.ai4j.model.openai.OpenAiModelName;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeReadabilityAnalyzeWithGpt4Action extends CodeReadabilityAnalyseAction {

    @Override
    protected OpenAiModelName getModelName() {
        return GPT_4;
    }
}
