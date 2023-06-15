package dev.ai4j.aid2.testcases;


public class GenerateTestCasesWithGpt3Action extends GenerateTestCasesAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}