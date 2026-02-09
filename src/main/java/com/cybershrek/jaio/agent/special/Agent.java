package com.cybershrek.jaio.agent.special;

import com.cybershrek.jaio.exception.AgentException;

public interface Agent<I, O> {

    /**
     * Processes the given input synchronously and returns the corresponding output.
     *
     * @param input the input data to be processed
     * @return the output result produced by the agent
     * @throws AgentException if an error occurs during processing
     */
    O prompt(I input) throws AgentException;
}