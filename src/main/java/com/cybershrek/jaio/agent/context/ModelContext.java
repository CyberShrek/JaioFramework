package com.cybershrek.jaio.agent.context;

import java.util.List;
import java.util.Map;

public interface ModelContext {

    void addMessage(Map<String, Object> message);

    List<Map<String, Object>> getMessages();
}