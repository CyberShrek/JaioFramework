package com.cybershrek.jaio.agent.context;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BasicModelContext implements ModelContext {

    protected final List<Object> messages;

    public BasicModelContext() {
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