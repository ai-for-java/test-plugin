package dev.ai4j.aid2.tests;



public class GenerateTestsWithGpt3Action extends GenerateTestsAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
