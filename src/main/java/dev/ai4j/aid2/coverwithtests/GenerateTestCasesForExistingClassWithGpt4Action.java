package dev.ai4j.aid2.coverwithtests;


public class GenerateTestCasesForExistingClassWithGpt4Action extends GenerateTestCasesForExistingClassAction {

    @Override
    protected String getModelName() {
        return "gpt-4-0613";
    }
}
