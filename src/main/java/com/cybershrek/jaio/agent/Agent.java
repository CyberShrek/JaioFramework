package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * The base class for agents that process input and produce output.
 * Agents are designed to handle synchronous and asynchronous prompting operations.
 *
 * @param <I> the type of input data processed by the agent
 * @param <O> the type of output data produced by the agent
 */
public abstract class Agent<I, O> {

    /**
     * Processes the given input synchronously and returns the corresponding output.
     *
     * @param input the input data to be processed
     * @return the output result produced by the agent
     * @throws AgentException if an error occurs during processing
     */
    public abstract O prompt(I input) throws AgentException;

    /**
     * Processes the given input asynchronously using the specified executor and returns
     * a {@link CompletableFuture} that will be completed with the output result.
     *
     * @param input the input data to be processed
     * @return a {@link CompletableFuture} that will be completed with the output result.
     *         If an {@link AgentException} occurs during processing, the future will
     *         be completed exceptionally with a {@link CompletionException} wrapping
     *         the original AgentException.
     */
    public CompletableFuture<O> promptAsync(I input) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return prompt(input);
            } catch (AgentException e) {
                throw new CompletionException(e);
            }
        });
    }
}