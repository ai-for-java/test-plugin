package dev.ai4j.aid2.explain.summary;



public class CodeSummarizingWithGpt3Action extends CodeSummarizingAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
