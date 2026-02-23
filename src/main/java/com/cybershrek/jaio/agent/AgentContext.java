package com.cybershrek.jaio.agent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class AgentContext {

    protected final List<Message> messages;

    void addMessage(Message message) {
        messages.add(message);
    }

    void addSystemMessage(String text) {
        messages.add(new Message(MessageRole.SYSTEM, text));
    }
    void addUserMessage(Object content) {
        messages.add(new Message(MessageRole.USER, content));
    }
    void addAssistantMessage(Object content) {
        messages.add(new Message(MessageRole.ASSISTANT, content));
    }

    public List<Message> getMessages(MessageRole role) {
        return messages.stream().filter(m -> Objects.equals(m.role(), role)).toList();
    }

    public record Message(MessageRole role,
                          Object content) { }

    public enum MessageRole {
        SYSTEM,
        USER,
        ASSISTANT;
    }
}