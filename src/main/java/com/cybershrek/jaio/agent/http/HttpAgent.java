package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.HttpAgentException;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

public abstract class HttpAgent<I, O> implements Agent<I, O> {

    protected final HttpClient client;
    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected HttpAgent(HttpClient client) {
        Objects.requireNonNull(client, "Client cannot be null");
        this.client = client;
    }

    protected HttpAgent() {
        this(DEFAULT_CLIENT);
    }

    @Override
    public O prompt(I input) throws HttpAgentException {
        try {
            HttpRequest request = buildRequest(input);
            return onResponse(client.send(request, HttpResponse.BodyHandlers.ofInputStream()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        }
    }

    protected abstract HttpRequest buildRequest(I input) throws IOException, HttpAgentException;

    protected abstract O readOkBody(InputStream body) throws IOException, HttpAgentException;

    protected O onSuccessResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        if (response.statusCode() == 200)
            return readOkBody(response.body());
        throw new HttpAgentException("Unhandled Success", response);
    }

    protected O onInformationalResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Informational", response);
    }

    protected O onRedirectionResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Redirection", response);
    }

    protected O onErrorResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Error", response);
    }

    protected O onResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        int code = response.statusCode();
        try {
            if (code >= 100 && code < 200)
                return onInformationalResponse(response);

            if (code >= 200 && code < 300)
                return onSuccessResponse(response);

            if (code >= 300 && code < 400)
                return onRedirectionResponse(response);

            return onErrorResponse(response);
        } finally {
            response.body().close();
        }
    }
}