package dev.ai4j.aid2.explain.comment;



public class CodeCommentingWithGpt3Action extends CodeCommentingAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
