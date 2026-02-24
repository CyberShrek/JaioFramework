package com.cybershrek.jaio.agent.api.rest;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.HttpAgentException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class HttpAgent<I, O> extends Agent<I, O> {

    protected final RestApiStrategy<O> strategy;

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    @Override
    public synchronized O prompt(I input) throws HttpAgentException {
        try {
            return strategy.readResponse(client
                    .sendAsync(strategy.buildRequest(HttpRequest.newBuilder()), HttpResponse.BodyHandlers.ofInputStream())
                    .get()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        } catch (ExecutionException e) {
            throw new HttpAgentException("Execution error during request", e);
        }
    }
}