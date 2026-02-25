package com.cybershrek.jaio.agent.api.rest;

import com.cybershrek.jaio.agent.context.BasicModelContext;
import com.cybershrek.jaio.agent.context.ModelContext;
import com.cybershrek.jaio.exception.ModelException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;


public class GenericRestModelStrategy<I, O> extends RestModelStrategy<I, O> {

    protected static final ObjectMapper jsonMapper = new ObjectMapper();

    public GenericRestModelStrategy(ModelContext context, RestModel model) {
        super(context, model);
    }

    @Override
    protected void onInput(I input) {
        if (context.getMessages().isEmpty())
            context.addMessage(Map.of(
                    "role", "system",
                    "content", model.instruction()
            ));
        context.addMessage(Map.of(
                "role", "user",
                "content", input
        ));
    }

    @Override
    public final HttpRequest buildRequest(HttpRequest.Builder builder) throws IOException {
        return builder
                .uri(URI.create(model.url()))
                .header("Authorization", "Bearer " + model.apiKey())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofMinutes(10))
                .POST(HttpRequest.BodyPublishers.ofString(getRequestBody()))
                .build();
    }

    protected String getRequestBody() throws IOException {
        return jsonMapper.writeValueAsString(Map.of(
                "model", model.name(),
                "messages", context.getMessages()
        ));
    }

    @Override
    protected O readSuccessResponse(HttpResponse<InputStream> response) throws IOException {
        return null;
    }

    @Override
    protected void onOutput(O output) {
        context.addMessage(Map.of(
                "role", "assistant",
                "content", output
        ));
    }
}
