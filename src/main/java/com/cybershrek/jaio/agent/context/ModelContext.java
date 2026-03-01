package com.cybershrek.jaio.agent.context;

import java.util.List;
import java.util.Map;

public interface ModelContext {

    List<Map<String, Object>> getMessages();

    void addMessage(String role, Object content);

    void setSystemMessage(String role, Object content);
}
