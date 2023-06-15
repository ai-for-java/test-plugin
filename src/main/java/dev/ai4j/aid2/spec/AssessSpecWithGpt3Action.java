package dev.ai4j.aid2.spec;



public class AssessSpecWithGpt3Action extends AssessSpecAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
