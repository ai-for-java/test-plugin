package dev.ai4j.aid2.codereadabilityanalyzer;


public class CodeReadabilityAnalyzeWithGpt4Action extends CodeReadabilityAnalyseAction {

    @Override
    protected String getModelName() {
        return "gpt-4-0613";
    }
}
