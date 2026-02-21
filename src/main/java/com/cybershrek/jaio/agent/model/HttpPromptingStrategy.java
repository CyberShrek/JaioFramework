package com.cybershrek.jaio.agent.model;

public interface HttpPromptingStrategy<I, O> extends PromptingStrategy<I, O> {

    String getMethod();
    String getEndpoint();
    String getContentType();
    String getAuthorization();
}