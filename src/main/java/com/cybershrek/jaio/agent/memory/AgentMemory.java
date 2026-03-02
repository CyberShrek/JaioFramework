package com.cybershrek.jaio.agent.memory;

import java.util.List;
import java.util.Map;

public interface AgentMemory {

    List<Map<String, Object>> getMessages();

    void addMessage(String role, Object content);

    void setSystemMessage(String role, Object content);
}
