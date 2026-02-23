package com.cybershrek.jaio.agent.http.strategy;

import com.cybershrek.jaio.agent.AgentContext;
import com.cybershrek.jaio.agent.http.HttpModel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
public abstract class ApiStrategy<O> {

    protected final AgentContext context;
    protected final HttpModel model;

    public abstract HttpRequest buildRequest(HttpRequest.Builder builder) throws IOException;

    public abstract O readResponse(HttpResponse<InputStream> response) throws IOException;
}
