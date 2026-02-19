package com.cybershrek.jaio.agent.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AgentMessage {

    private final String role;
    private final Object content;
}
