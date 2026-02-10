package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.AgentException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public abstract class HttpAgent<I, O> implements Agent<I, O> {

    protected final Function<I, HttpRequest.Builder> reqBuilderFn;
    protected final Function<InputStream,         O> resBodyFn;
    protected HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    protected HttpAgent(Function<I, HttpRequest.Builder> reqBuilderFn,
                        Function<InputStream,         O> resBodyFn) {
        this.reqBuilderFn = reqBuilderFn;
        this.resBodyFn    = resBodyFn;
    }

    @Override
    public O prompt(I input) throws AgentException {
        try {
            return resBodyFn.apply(
                    client.send(
                            reqBuilderFn.apply(input).build(),
                            HttpResponse.BodyHandlers.ofInputStream()
                    ).body()
            );
        } catch (IOException | InterruptedException e) {
            throw new AgentException(e);
        }
    }
}