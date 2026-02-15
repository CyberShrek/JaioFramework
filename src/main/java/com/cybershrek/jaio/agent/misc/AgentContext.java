package com.cybershrek.jaio.agent.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class AgentContext {

    protected final String instruction;
    protected final List<Message> messages = new LinkedList<>();

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addMessage(String role, Object content) {
        messages.add(new Message(role, content));
    }

    public List<Message> getMessages(String role) {
        return messages.stream().filter(m -> Objects.equals(m.getRole(), role)).toList();
    }

    @RequiredArgsConstructor
    @Getter
    public static class Message {

        private final String role;
        private final Object content;
    }
}