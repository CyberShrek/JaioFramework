package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.agent.api.ApiStrategy;
import com.cybershrek.jaio.agent.context.AgentContext;
import com.cybershrek.jaio.agent.context.BasicAgentContext;
import com.cybershrek.jaio.exception.AgentException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Agent<I, O> {

    protected final AgentContext context;
    protected final ApiStrategy<O> strategy;

    public Agent(ApiStrategy<O> strategy) {
        this(new BasicAgentContext(), strategy);
    }

    public O prompt(I input) throws AgentException {

    };
}