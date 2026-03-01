package com.cybershrek.jaio.agent;

import com.cybershrek.jaio.exception.AgentException;
import com.cybershrek.jaio.exception.HttpAgentException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class HttpAgent<I, O> extends Agent<I, O> {

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    protected Consumer<O> generationCallback;

    @Override
    public final synchronized O prompt(I input) throws AgentException {
        try {
            return readResponse(sendRequest(requestOnInput(input)));
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        }
    }

    @Override
    public final synchronized O prompt(I input, Consumer<O> generationCallback) throws AgentException {
        this.generationCallback = generationCallback;
        var result = prompt(input);
        if (this.generationCallback != null)
            this.generationCallback.accept(result);

        this.generationCallback = null;
        return result;
    };

    abstract protected HttpRequest requestOnInput(I input) throws IOException;

    abstract protected O readSuccessResponse(HttpResponse<InputStream> response) throws IOException;

    protected HttpResponse<InputStream> sendRequest(HttpRequest request) throws IOException {
        try {
            return client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (ExecutionException e) {
            throw new HttpAgentException("Execution error during request", e);
        }
    }

    private O readResponse(HttpResponse<InputStream> response) throws IOException {
        int code = response.statusCode();
        try {
            if (code >= 100 && code < 200) return readInformationalResponse(response);
            if (code >= 200 && code < 300) return readSuccessResponse(response);
            if (code >= 300 && code < 400) return readRedirectionResponse(response);
            return readErrorResponse(response);
        } finally {
            response.body().close();
        }
    }

    protected O readInformationalResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpAgentException("Informational", response);
    }

    protected O readRedirectionResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpAgentException("Redirection", response);
    }

    protected O readErrorResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpAgentException("Error", response);
    }
}
