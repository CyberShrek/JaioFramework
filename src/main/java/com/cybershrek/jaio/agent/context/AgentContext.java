package com.cybershrek.jaio.agent.context;

import java.util.List;

public interface AgentContext {

    public void addMessage(Object message);

    public List<Object> getMessages();
}