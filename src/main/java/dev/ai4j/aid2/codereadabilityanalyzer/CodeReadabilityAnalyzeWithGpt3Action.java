package dev.ai4j.aid2.codereadabilityanalyzer;



public class CodeReadabilityAnalyzeWithGpt3Action extends CodeReadabilityAnalyseAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
