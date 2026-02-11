package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.AgentException;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpAgent<I, O> implements Agent<I, O> {

    protected HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public O prompt(I input) throws AgentException {
        try {
            return handleResponse(
                    client.send(
                            buildRequest(input),
                            HttpResponse.BodyHandlers.ofInputStream()
                    )
            );
        } catch (Exception e) {
            throw new AgentException(e);
        }
    }

    protected abstract HttpRequest buildRequest(I input) throws Exception;

    protected abstract O handleResponse(HttpResponse<InputStream> response) throws Exception;
}