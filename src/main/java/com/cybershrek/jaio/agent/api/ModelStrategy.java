package com.cybershrek.jaio.agent.api;

import com.cybershrek.jaio.agent.context.ModelContext;
import com.cybershrek.jaio.exception.ModelException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ModelStrategy<I, O> {

    protected final ModelContext context;

    abstract public O prompt(I input) throws ModelException;
}