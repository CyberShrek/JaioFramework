package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.agent.memory.DefaultAgentMemory;
import com.cybershrek.jaio.agent.memory.AgentMemory;
import com.cybershrek.jaio.exception.AgentException;

import java.util.function.Consumer;

abstract public class Agent<I, O> {

    protected final AgentMemory memory;

    protected Agent(AgentMemory memory) {
        this.memory = memory;
    }
    public Agent() {
        this(new DefaultAgentMemory());
    }

    abstract public O prompt(I input) throws AgentException;

    abstract public O prompt(I input, Consumer<O> generationCallback) throws AgentException;

//    public abstract void stopGeneration();
//    public abstract void continueGeneration();
}