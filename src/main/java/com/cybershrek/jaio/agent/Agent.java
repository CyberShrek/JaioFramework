package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;

public interface Agent<I, O> {

    O prompt(I input) throws AgentException;
}