package com.cybershrek.jaio.agent.api.rest;

import com.cybershrek.jaio.agent.context.BasicAgentContext;
import com.cybershrek.jaio.exception.HttpAgentException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public abstract class GenericRestApiStrategy<O> extends RestApiStrategy<O> {

    protected static final ObjectMapper jsonMapper = new ObjectMapper();

    public GenericRestApiStrategy(BasicAgentContext context, RestApiModel model) {
        super(context, model);
    }

    public final HttpRequest buildRequest(HttpRequest.Builder builder) throws IOException {
        return builder
                .uri(URI.create(model.url()))
                .header("Authorization", "Bearer " + model.apiKey())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofMinutes(10))
                .POST(HttpRequest.BodyPublishers.ofByteArray(jsonMapper.writeValueAsBytes(getRequestBody())))
                .build();
    }

    protected abstract HttpRequest.BodyPublisher getRequestBody() throws IOException;

    public final O readResponse(HttpResponse<InputStream> response) throws IOException {
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

    protected abstract O readOkResponseBody(InputStream body) throws IOException;

    protected O readInformationalResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpAgentException("Informational", response);
    }

    protected O readSuccessResponse(HttpResponse<InputStream> response) throws IOException {
        if (response.statusCode() == 200)
            return readOkResponseBody(response.body());
        throw new HttpAgentException("Unhandled Success", response);
    }

    protected O readRedirectionResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpAgentException("Redirection", response);
    }

    protected O readErrorResponse(HttpResponse<InputStream> response) throws IOException {
        throw new HttpAgentException("Error", response);
    }
}
