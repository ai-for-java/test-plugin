package dev.ai4j.aid2.impl;


public class GenerateImplementationWithGpt3Action extends GenerateImplementationAction {

    @Override
    protected String getModelName() {
        return "gpt-3.5-turbo-0613";
    }
}
