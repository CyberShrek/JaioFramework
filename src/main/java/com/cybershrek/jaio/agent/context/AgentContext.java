package com.cybershrek.jaio.agent.context;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public class AgentContext {

    protected final List<AgentMessage> messages = new LinkedList<>();

    public void addMessage(AgentMessage message) {
        messages.add(message);
    }

    public void addMessage(String role, Object content) {
        messages.add(new AgentMessage(role, content));
    }
    public void addSystemMessage(Object content) {
        addMessage("system", content);
    }
    public void addUserMessage(Object content) {
        addMessage("user", content);
    }
    public void addAssistantMessage(Object content) {
        addMessage("assistant", content);
    }

    public List<AgentMessage> getMessages(String role) {
        return messages.stream().filter(m -> Objects.equals(m.getRole(), role)).toList();
    }
}