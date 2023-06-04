package com.example.testplugin.codereadabilityanalyzer;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class CodeReadabilityAnalyzeWithGpt4Action extends CodeReadabilityAnalyseAction {

    @Override
    protected String getModelName() {
        return GPT_4;
    }
}
