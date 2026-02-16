package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Agent<I, O> {

    protected final AgentContext context;

    public Agent() {
        context = new AgentContext();
    }

    public synchronized final O prompt(I input) throws AgentException {
        addUserMessage(input);
        O result = getResult();
        addAgentMessage(result);
        return result;
    };

    abstract protected void addUserMessage(I input);

    abstract protected O getResult() throws AgentException;

    abstract protected void addAgentMessage(O output);
}