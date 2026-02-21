package com.cybershrek.jaio.agent.model;

public interface PromptingStrategy<I, O> {

    O prompt(I input);

    void onInput(I input);
    void onOutput(O output);
}
