package dev.ai4j.aid2.coverwithtests;


public class GenerateTestsFromTestCasesWithGpt4Action extends GenerateTestsFromTestCasesAction {

    @Override
    protected String getModelName() {
        return "gpt-4-0613";
    }
}
