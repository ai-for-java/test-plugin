package dev.ai4j.aid2.refactoring;



public class CodeRefactoringWithGpt3Action extends CodeRefactoringAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
