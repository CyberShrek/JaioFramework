package com.cybershrek.jaio.agent.http;

import lombok.Builder;

@Builder
public class HttpAgentConfig {

    private final String url;
    private final String apiKey;
    private final String model;

}
