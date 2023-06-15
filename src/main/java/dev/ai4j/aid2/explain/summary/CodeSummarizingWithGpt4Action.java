package dev.ai4j.aid2.explain.summary;


public class CodeSummarizingWithGpt4Action extends CodeSummarizingAction {

    @Override
    protected String getModelName() {
        return "gpt-4-0613";
    }
}
