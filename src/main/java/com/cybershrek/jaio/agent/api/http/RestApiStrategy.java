package com.cybershrek.jaio.agent.api.http;

import com.cybershrek.jaio.agent.context.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

public abstract class RestApiStrategy<I, O> extends HttpApiStrategy<I, O> {

    protected static final ObjectMapper jsonMapper = new ObjectMapper();

    public RestApiStrategy(ModelContext context, RestModel model) {
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

    protected class CallSteps {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        public Request prepareRequest(String url) {
            requestBuilder.uri(URI.create(url));
            return new Request(url);
        }

        @RequiredArgsConstructor
        private class Request {
            private final String url;

            public Request withHeader(String name, String value) {
                requestBuilder.header(name, value);
                return this;
            }

            public Request withAuthorization(String authScheme, String value) {
                return withHeader("Authorization", authScheme + " " + value);
            }
            public Request withAuthorization(String value) {
                return withAuthorization(value);
            }
            public Request withAuthorizationBearer(String value) {
                return withAuthorization("Bearer", value);
            }

            public Response send(String contentType, String body) {
                withHeader("Content-Type", "application/json");
                return new Response();
            }
            public Response sendJson(String body) {
                return send("application/json", body);
            }
        }

        @Builder
        private class Response {

            public O accept(String contentType, Function<InputStream, O> callback) {
                requestBuilder.header("Accept", contentType);
                return callback.apply(null);
            }
            public O acceptJson(Function<InputStream, O> callback) {
                return accept("application/json", callback);
            }
        }
    }
}