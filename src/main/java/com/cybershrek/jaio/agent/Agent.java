package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.agent.context.DefaultModelContext;
import com.cybershrek.jaio.agent.context.ModelContext;
import com.cybershrek.jaio.exception.AgentException;

import java.util.function.Consumer;

abstract public class Agent<I, O> {

    protected final ModelContext context;

    protected Agent(ModelContext context) {
        this.context = context;
    }
    public Agent() {
        this(new DefaultModelContext());
    }

    abstract public O prompt(I input) throws AgentException;

    abstract public O prompt(I input, Consumer<O> generationCallback) throws AgentException;

//    public abstract void stopGeneration();
//    public abstract void continueGeneration();
}