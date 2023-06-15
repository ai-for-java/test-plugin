package dev.ai4j.aid2.coverwithtests;



public class GenerateTestCasesForExistingClassWithGpt3Action extends GenerateTestCasesForExistingClassAction {

    @Override
    protected String getModelName() {
        return "gpt-4-0613";
    }
}
