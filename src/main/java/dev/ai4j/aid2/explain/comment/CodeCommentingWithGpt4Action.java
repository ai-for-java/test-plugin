package dev.ai4j.aid2.explain.comment;


public class CodeCommentingWithGpt4Action extends CodeCommentingAction {

    @Override
    protected String getModelName() {
        return "gpt-4-0613";
    }
}
