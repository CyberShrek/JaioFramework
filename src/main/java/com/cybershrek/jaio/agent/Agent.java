package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Agent<I, O> {

    protected final AgentContext context;

    public Agent() {
        this(new AgentContext());
    }

    public synchronized final O prompt(I input) throws AgentException {
        onInput(input);
        O output = requestOutput();
        onOutput(output);
        return output;
    };

    abstract protected void onInput(I content)  throws AgentException;

    abstract protected O requestOutput()        throws AgentException;

    abstract protected void onOutput(O content) throws AgentException;
}