package com.cybershrek.jaio.agent.api;

import com.cybershrek.jaio.agent.context.AgentContext;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public abstract class ApiStrategy <I, O> {

    protected final AgentContext context;

    abstract O prompt() throws IOException;
}