package com.cybershrek.jaio.agent.api.rest;

import com.cybershrek.jaio.agent.context.BasicAgentContext;
import com.cybershrek.jaio.agent.api.ApiStrategy;
import com.cybershrek.jaio.exception.AgentException;
import com.cybershrek.jaio.exception.HttpAgentException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public abstract class RestApiStrategy<O> implements ApiStrategy<O> {

    protected final BasicAgentContext context;
    protected final RestApiModel model;

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    @Override
    public O prompt(BasicAgentContext context) throws AgentException {
        try {
            return readResponse(client
                    .sendAsync(buildRequest(HttpRequest.newBuilder()), HttpResponse.BodyHandlers.ofInputStream())
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

    public abstract HttpRequest buildRequest(HttpRequest.Builder builder) throws IOException;

    public abstract O readResponse(HttpResponse<InputStream> response) throws IOException;
}
