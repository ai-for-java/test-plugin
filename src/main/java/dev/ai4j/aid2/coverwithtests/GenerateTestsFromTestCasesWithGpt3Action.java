package dev.ai4j.aid2.coverwithtests;


public class GenerateTestsFromTestCasesWithGpt3Action extends GenerateTestsFromTestCasesAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
