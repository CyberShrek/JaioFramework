package com.cybershrek.jaio.agent.memory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultAgentMemory implements AgentMemory {

    protected Map<String, Object> systemMessage;
    protected LinkedList<Map<String, Object>> miscMessages;

    public DefaultAgentMemory() { this(List.of()); }
    public DefaultAgentMemory(List<Map<String, Object>> messages) {
        miscMessages = new LinkedList<>(messages);
    }

    @Override
    public List<Map<String, Object>> getMessages() {
        var messages = new ArrayList<Map<String, Object>>();
        if (systemMessage != null)
            messages.add(systemMessage);
        messages.addAll(miscMessages);
        return messages;
    }

    @Override
    public void addMessage(String role, Object content) {
        miscMessages.add(mapMessage(role, content));
    }

    @Override
    public void setSystemMessage(String role, Object content) {
        systemMessage = mapMessage(role, content);
    }

    protected Map<String, Object> mapMessage(String role, Object content) {
        return Map.of("role", role, "content", content);
    }

    protected void addMessage(Map<String, Object> message) {
        miscMessages.add(message);
    }
}