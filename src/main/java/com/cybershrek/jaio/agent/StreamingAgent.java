package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;

import java.util.function.Consumer;

public abstract class StreamingAgent<I, O, C> extends Agent<I, O> {

    private Consumer<C> chunkConsumer;

    O prompt(I input, Consumer<C> consumer) throws AgentException {
        this.chunkConsumer = chunkConsumer;
        return prompt(input);
    };
}