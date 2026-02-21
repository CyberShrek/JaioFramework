package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.agent.context.AgentContext;
import com.cybershrek.jaio.agent.model.PromptingStrategy;
import com.cybershrek.jaio.exception.AgentException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Agent<I, O> {

    protected final AgentContext context;

    public Agent() {
        this(new AgentContext());
    }

    public synchronized O prompt(I input, PromptingStrategy<I, O> strategy) throws AgentException{
        strategy.onInput(input);
        var output = strategy.prompt(input);
        strategy.onOutput(output);
        return output;
    };
}