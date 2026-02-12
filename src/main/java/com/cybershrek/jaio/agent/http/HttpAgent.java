package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.HttpAgentException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public abstract class HttpAgent<I, O> implements Agent<I, O> {

    protected final HttpClient client;
    protected HttpAgent(HttpClient client) {
        this.client = Objects.requireNonNull(client, "HttpClient must not be null");
    }

    @Override
    public O prompt(I input) throws HttpAgentException {
        try {
            HttpRequest request = buildRequest(input);
            Objects.requireNonNull(request, "buildRequest() returned null");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        }
    }

    protected abstract HttpRequest buildRequest(I input) throws IOException, HttpAgentException;

    protected abstract O onSuccess(HttpResponse<String> response) throws IOException, HttpAgentException;

    protected O onInformational(HttpResponse<String> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Informational", response);
    }

    protected O onRedirection(HttpResponse<String> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Redirection", response);
    }

    protected O onError(HttpResponse<String> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Error", response);
    }

    protected O handleResponse(HttpResponse<String> response) throws IOException, HttpAgentException {
        var code = response.statusCode();

        if (code >= 100 && code < 200)
            return onInformational(response);

        if (code >= 200 && code < 300)
            return onSuccess(response);

        if (code >= 300 && code < 400)
            return onRedirection(response);

        return onError(response);
    }
}