package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;

public abstract class Agent<I, O> {

    protected AgentContext context;

    abstract public O prompt(I input) throws AgentException;

    protected void useContext(AgentContext context) {
        this.context = context;
    }
}