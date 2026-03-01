package com.cybershrek.jaio.agent;
import com.cybershrek.jaio.exception.HttpAgentException;

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
import java.util.List;

public abstract class RestApiAgent<I, O> extends HttpAgent<I, O> {

    private final Config config;
    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    private ThrowingFunction<InputStream, O> readResponseBody;

    protected RestApiAgent() {
        configure(this.config = new Config());
    }

    protected void configure(Config config) {
        config
                .requestAttempts(5, 1000)
                .requestTimeoutInMinutes(10);
    };

    abstract protected void onInput(I input, Steps strategy) throws IOException;

    @Override
    protected final HttpRequest requestOnInput(I input) throws IOException {
        Steps steps = new Steps();
        onInput(input, steps);
        return requestBuilder
                .build();
    }

    @Override
    protected final HttpResponse<InputStream> sendRequest(HttpRequest request) throws IOException {
        HttpResponse<InputStream> response = null;
        for (int i = 0; i < config.retryCount; i++) {
            response = super.sendRequest(request);
            if (response != null && response.statusCode() == 200) {
                break;
            }
            try {
                Thread.sleep(config.retryDelayInMillis);
            } catch (InterruptedException e) {
                throw new HttpAgentException("Request interrupted", e);
            }
        }
        return response;
    }

    @Override
    protected final O readSuccessResponse(HttpResponse<InputStream> response) throws IOException {
        if (readResponseBody == null) throw new IllegalStateException("Response body callback has not been set");
        return readResponseBody.apply(response.body());
    }

    public class Config {

        int retryCount;
        int retryDelayInMillis;

        public Config requestTimeoutInMillis(long millis) {
            return requestTimeout(Duration.ofMillis(millis));
        };
        public Config requestTimeoutInSeconds(int seconds) {
            return requestTimeout(Duration.ofSeconds(seconds));
        };
        public Config requestTimeoutInMinutes(int minutes) {
            return requestTimeout(Duration.ofMinutes(minutes));
        };
        private Config requestTimeout(Duration duration) {
            requestBuilder.timeout(duration);
            return this;
        };

        public Config requestAttempts(int count) {
            this.retryCount = count;
            return this;
        }
        public Config requestAttempts(int count, int delayInMillis) {
            this.retryCount = count;
            this.retryDelayInMillis = delayInMillis;
            return this;
        }
    }

    public class Steps {

        public Request toUrl(String url) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL: " + url, e);
            }
            requestBuilder.uri(URI.create(url));
            return new Request();
        }

        public class Request {

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
                withHeader("Content-Type", contentType);
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
                return new Response();
            }
            public Response sendJson(String body) {
                return send("application/json", body);
            }
        }

        public class Response {

            public void thenAccept(String contentType, ThrowingFunction<InputStream, O> readBody) {
                requestBuilder.header("Accept", contentType);
                readResponseBody = readBody;
            }
            public void thenAcceptJson(ThrowingFunction<InputStream, O> readBody) {
                thenAccept("application/json", readBody);
            }
            public void thenAcceptSseChunks(ThrowingFunction<List<String>, O> mergeChunks) {
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
}