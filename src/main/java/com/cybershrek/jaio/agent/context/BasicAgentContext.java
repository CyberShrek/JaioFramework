package com.cybershrek.jaio.agent.context;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BasicAgentContext implements AgentContext {

    protected final List<Object> messages;

    public BasicAgentContext() {
        this(new ArrayList<>());
    }

    @Override
    public List<Object> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(Object message) {
        messages.add(message);
    }
}