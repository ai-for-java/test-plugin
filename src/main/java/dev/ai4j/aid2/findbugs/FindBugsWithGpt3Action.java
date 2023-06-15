package dev.ai4j.aid2.findbugs;


public class FindBugsWithGpt3Action extends FindBugsAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
