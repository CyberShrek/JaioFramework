package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public abstract class Agent<I, O> {

    protected final AgentContext context;

    public Agent() {
        this(new AgentContext(new ArrayList<>()));
    }

    public abstract O prompt(I input) throws AgentException;
}