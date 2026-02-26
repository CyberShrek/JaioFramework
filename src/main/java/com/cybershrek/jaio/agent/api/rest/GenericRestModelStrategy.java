package com.cybershrek.jaio.agent.api.rest;

import com.cybershrek.jaio.agent.context.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public abstract class GenericRestModelStrategy<I, O> extends RestModelStrategy<I, O> {

    protected static final ObjectMapper jsonMapper = new ObjectMapper();

    public GenericRestModelStrategy(ModelContext context, RestModel model) {
        super(context, model);
        if (model.instruction() != null && context.getMessages().isEmpty())
            context.addMessage(Map.of(
                    "role", "system",
                    "content", model.instruction()
            ));
    }

    @Override
    protected HttpRequest onInputBuildRequest(I input, HttpRequest.Builder builder) throws IOException {
        context.addMessage(Map.of(
                "role", "user",
                "content", input
        ));
        return builder
                .uri(URI.create(model.url()))
                .header("Authorization", "Bearer " + model.apiKey())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofMinutes(10))
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(Map.of(
                        "model", model.name(),
                        "messages", context.getMessages()
                ))))
                .build();
    }

    @Override
    protected O readSuccessResponse(HttpResponse<InputStream> response) throws IOException {
        var output = readOkResponseBody(response.body());
        context.addMessage(Map.of(
                "role", "assistant",
                "content", output
        ));
        return output;
    }
    protected abstract O readOkResponseBody(InputStream body) throws IOException;
}
