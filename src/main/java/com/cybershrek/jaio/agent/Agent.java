package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.agent.context.AgentContext;
import com.cybershrek.jaio.exception.AgentException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Agent<I, O> {

    protected final AgentContext context;

    public Agent() {
        this(new AgentContext());
    }

    public abstract O prompt(I input) throws AgentException;
}