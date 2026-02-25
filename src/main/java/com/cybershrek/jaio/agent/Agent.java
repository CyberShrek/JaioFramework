package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.agent.api.ModelStrategy;
import com.cybershrek.jaio.exception.ModelException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Agent<I, O> {

    protected final ModelStrategy<I, O> strategy;

    public O prompt(I input) throws ModelException {
        return strategy.prompt(input);
    };
}