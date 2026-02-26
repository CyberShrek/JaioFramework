package com.cybershrek.jaio.agent.api.rest;

import com.cybershrek.jaio.agent.api.ModelStrategy;
import com.cybershrek.jaio.agent.context.ModelContext;
import com.cybershrek.jaio.exception.ModelException;
import com.cybershrek.jaio.exception.HttpModelException;
import lombok.Builder;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public abstract class RestModelStrategy<I, O> extends ModelStrategy<I, O> {

    protected final RestModel model;

    public RestModelStrategy(ModelContext context,
                             RestModel model) {
        super(context);
        this.model = model;
    }

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    @Override
    public final synchronized O prompt(I input) throws ModelException {
        try {
            return readResponse(client
                    .sendAsync(onInputBuildRequest(input, HttpRequest.newBuilder()), HttpResponse.BodyHandlers.ofInputStream())
                    .get()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpModelException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpModelException("I/O error during request", e);
        } catch (ExecutionException e) {
            throw new HttpModelException("Execution error during request", e);
        }
    }

    protected abstract void onInput(I input, ApiChain chain);

    protected abstract HttpRequest onInputBuildRequest(I input, HttpRequest.Builder builder) throws IOException;

    protected abstract O readSuccessResponse(HttpResponse<InputStream> response) throws IOException;

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
        throw new HttpModelException("Informational", response);
    }

    protected O readRedirectionResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpModelException("Redirection", response);
    }

    protected O readErrorResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpModelException("Error", response);
    }

    protected class ApiChain {

        public Request url(String url) {
            return Request.builder().url(url).build();
        }

        public ApiChain buildResponse() {
            return this;
        }

        @Builder
        public class Request {
            protected final String url;
        }

        @Builder
        public class Response {

        }
    }
}
