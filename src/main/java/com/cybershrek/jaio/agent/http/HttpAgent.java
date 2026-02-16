package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.agent.AgentContext;
import com.cybershrek.jaio.exception.HttpAgentException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public abstract class HttpAgent<I, O> extends Agent<I, O> {

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    @Override
    protected O getResult() throws HttpAgentException {
        try {
            HttpRequest request = buildRequest();
            return onResponse(
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        } catch (ExecutionException e) {
            throw new HttpAgentException("Execution error during request", e);
        }
    }

    protected abstract HttpRequest buildRequest() throws IOException, HttpAgentException;

    protected abstract O readOkBody(InputStream body) throws IOException, HttpAgentException;

    protected O onSuccessResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        if (response.statusCode() == 200)
            return readOkBody(response.body());
        throw new HttpAgentException("Unhandled Success", response);
    }

    protected O onErrorResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Error", response);
    }

    protected O onResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        int code = response.statusCode();
        try {
            if (code >= 100 && code < 200)
                throw new HttpAgentException("Informational", response);

            if (code >= 200 && code < 300)
                return onSuccessResponse(response);

            if (code >= 300 && code < 400)
                throw new HttpAgentException("Redirection", response);

            return onErrorResponse(response);
        } finally {
            response.body().close();
        }
    }
}