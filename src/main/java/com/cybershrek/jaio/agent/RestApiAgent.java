package com.cybershrek.jaio.agent;
import com.cybershrek.jaio.exception.HttpAgentException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class RestApiAgent<I, O> extends HttpAgent<I, O> {

    private final Configurator configurator;
    private RequestConfigurator requestConfigurator;

    protected RestApiAgent() {
        configure(this.configurator = new Configurator());
    }

    abstract protected void configure(Configurator configurator);

    abstract protected void onInput(I input, RequestConfigurator requestConfigurator) throws IOException;

    @Override
    protected final HttpRequest requestOnInput(I input) throws IOException {
        onInput(input, requestConfigurator = new RequestConfigurator());
        var builder = HttpRequest.newBuilder(URI.create(requestConfigurator.url))
                .POST(HttpRequest.BodyPublishers.ofString(requestConfigurator.body));
        requestConfigurator.headers.forEach(builder::header);
        return builder.build();
    }

    @Override
    protected final HttpResponse<InputStream> sendRequest(HttpRequest requester) throws IOException {
        HttpResponse<InputStream> response = null;
        for (int i = 0; i < configurator.attemptsCount; i++) {
            if (configurator.requestLogger != null)
                configurator.requestLogger.accept(requester);
            response = super.sendRequest(requester);
            if (response != null) {
                if (configurator.responseLogger != null)
                    configurator.responseLogger.accept(response);
                if (response.statusCode() == 200)
                    break;
            }
            if (i < configurator.attemptsCount - 1) {
                try {
                    Thread.sleep(configurator.attemptDelayInMillis);
                } catch (InterruptedException e) {
                    throw new HttpAgentException("Request interrupted", e);
                }
            }
        }
        return response;
    }

    @Override
    protected final O readSuccessResponse(HttpResponse<InputStream> response) throws IOException {
        if (requestConfigurator.readResponseBody == null) throw new IllegalStateException("Response body callback has not been set");
        return requestConfigurator.readResponseBody.apply(response.body());
    }

    public class Configurator {

        int attemptsCount        = 3;
        int attemptDelayInMillis = 1000;

        Duration requestTimeout = Duration.ofMinutes(10);

        ThrowingConsumer<HttpRequest> requestLogger;
        ThrowingConsumer<HttpResponse<InputStream>> responseLogger;

        public Configurator requestTimeoutInMillis(long millis) {
            return requestTimeout(Duration.ofMillis(millis));
        }
        public Configurator requestTimeoutInSeconds(int seconds) {
            return requestTimeout(Duration.ofSeconds(seconds));
        }
        public Configurator requestTimeoutInMinutes(int minutes) {
            return requestTimeout(Duration.ofMinutes(minutes));
        }
        private Configurator requestTimeout(Duration duration) {
            this.requestTimeout = duration;
            return this;
        }

        public Configurator requestAttempts(int count) {
            this.attemptsCount = count;
            return this;
        }
        public Configurator requestAttempts(int count, int delayInMillis) {
            this.attemptsCount = count;
            this.attemptDelayInMillis = delayInMillis;
            return this;
        }

        public Configurator requestLog(boolean enable) {
            requestLogger = enable ? (request) -> log.info(""" 
                            
                            \tRequest:     \t{}
                            \tContent-Type:\t{}
                            \tAccept:      \t{}
                            \tBody:        \t{}""",
                    request,
                    request.headers().firstValue("Content-Type").orElse("not set"),
                    request.headers().firstValue("Accept").orElse("not set"),
                    requestConfigurator.body) : null;
            return this;
        }
        public Configurator responseLog(boolean enable) {
            responseLogger = enable ? (response) -> log.info("""
                    
                    \tResponse:     \t{}
                    \tBody:         \t{}""",
                    response,
                    new String(response.body().readAllBytes())) : null;
            return this;
        }
    }

    public class RequestConfigurator {

        String url;
        Map<String, String> headers = new HashMap<>();
        String body;

        ThrowingFunction<InputStream, O> readResponseBody;

        public Request toUrl(String url) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL: " + url, e);
            }
            this.url = url;
            return new Request();
        }

        public class Request {

            public Request withHeader(String name, String value) {
                headers.put(name, value);
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

            public Response send(String contentType, String payload) {
                withHeader("Content-Type", contentType);
                body = payload;
                return new Response();
            }
            public Response sendJson(String body) {
                return send("application/json", body);
            }
        }

        public class Response {

            public void thenAccept(String contentType, ThrowingFunction<InputStream, O> readBody) {
                headers.put("Accept", contentType);
                readResponseBody = readBody;
            }
            public void thenAcceptJson(ThrowingFunction<InputStream, O> readBody) {
                thenAccept("application/json", readBody);
            }
            public void thenAcceptSseStream(ThrowingFunction<List<String>, O> mergeChunks) {
                thenAccept("text/event-stream", body -> {
                    var chunks = new ArrayList<String>();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(body))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String chunk = prepareChunkData(line);
                            if (chunk != null) {
                                chunks.add(chunk);
                                if (generationCallback != null)
                                    generationCallback.accept(mergeChunks.apply(chunks));
                            }
                        }
                    }
                    generationCallback = null;
                    return mergeChunks.apply(chunks);
                });
            }
            private String prepareChunkData(String line) {
                if (line.startsWith("data: ")) {
                    line = line.substring(6);
                }
                if (line.startsWith("{") && line.endsWith("}")) {
                    return line;
                }
                return null;
            }
        }
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws IOException;
    }
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws IOException;
    }
}