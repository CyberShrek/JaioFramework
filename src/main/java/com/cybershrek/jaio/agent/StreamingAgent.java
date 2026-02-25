package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.ModelException;

import java.util.function.Consumer;

public abstract class StreamingAgent<I, O, C> extends Agent<I, O> {

    private Consumer<C> chunkConsumer;

    O prompt(I input, Consumer<C> consumer) throws ModelException {
        this.chunkConsumer = chunkConsumer;
        return prompt(input);
    };
}