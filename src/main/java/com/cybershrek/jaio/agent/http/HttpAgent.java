package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.AgentException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public abstract class HttpAgent<I, O> implements Agent<I, O> {

    protected final Function<I, HttpRequest.Builder> reqBuilderFn;
    protected final Function<String, O>            resBodyFn;
    protected final HttpResponse.BodyHandler<O>      resBodyHandler;
    protected HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    protected HttpAgent(Function<I, HttpRequest.Builder> reqBuilderFn,
                        Function<String,              O> resBodyFn) {
        this.reqBuilderFn   = reqBuilderFn;
        this.resBodyFn      = resBodyFn;
    }

    protected HttpAgent(Function<I, HttpRequest.Builder> reqBuilderFn,
                        HttpResponse.BodyHandler<O>      resBodyHandler) {

        this.reqBuilderFn   = reqBuilderFn;
        this.resBodyHandler = resBodyHandler;
    }

    @Override
    public O prompt(I input) throws AgentException {
        try {
            return client.send(reqBuilderFn.apply(input).build(), resBodyHandler).body();
        } catch (IOException | InterruptedException e) {
            throw new AgentException(e);
        }
    }
}