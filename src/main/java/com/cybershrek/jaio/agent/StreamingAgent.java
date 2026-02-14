package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;

import java.util.function.Consumer;

public interface StreamingAgent<I, O, C> {

    O prompt(I input, Consumer<C> consumer) throws AgentException;
}