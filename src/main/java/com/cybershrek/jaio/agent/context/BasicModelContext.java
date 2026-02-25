package com.cybershrek.jaio.agent.context;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BasicModelContext implements ModelContext {

    protected final List<Map<String, Object>> messages;

    public BasicModelContext() {
        this(new ArrayList<>());
    }

    @Override
    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(Map<String, Object> message) {
        messages.add(message);
    }
}