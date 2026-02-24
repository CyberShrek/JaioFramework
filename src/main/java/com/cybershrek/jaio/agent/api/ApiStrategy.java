package com.cybershrek.jaio.agent.api;

import com.cybershrek.jaio.agent.AgentContext;
import com.cybershrek.jaio.exception.AgentException;

public interface ApiStrategy <O> {

    O process(AgentContext context) throws AgentException;
}
