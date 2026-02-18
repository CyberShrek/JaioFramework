package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.HttpAgentException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class HttpAgent<I, O> extends Agent<I, O> {

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    @Override
    protected O requestOutput() throws HttpAgentException {
        try {
            Configurator configurator = new Configurator();
            configureRequest(configurator);
            return onResponse(client
                    .sendAsync(configurator.build(), HttpResponse.BodyHandlers.ofInputStream())
                    .get()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        } catch (ExecutionException e) {
            throw new HttpAgentException("Execution error during request", e);
        }
    }
    protected void configureRequest(Configurator configurator) throws IOException {};


    protected void configureRequest(Configurator configurator) throws IOException {};

    protected O readOkBody(InputStream body) throws IOException {
        return null;
    }

    protected void onInput(String content) {
        context.addMessage("user", content);
    }

    protected void onOutput(String content) {
        context.addMessage("assistant", content);
    }

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

    protected class Configurator {

        private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        public Request url(String url) {
            requestBuilder.uri(URI.create(url));
            return new Request();
        }

        protected HttpRequest build() {
            return requestBuilder.build();
        }

        public class Request {

            private Request() {
                contentType("application/json");
            }

            public Request timeoutInSeconds(Integer timeoutInSeconds) {
                requestBuilder.timeout(Duration.ofSeconds(timeoutInSeconds));
                return this;
            }

            public Request header(String name, String value) {
                requestBuilder.header(name, value);
                return this;
            }

            public Request contentType(String contentType, String accept) {
                requestBuilder
                        .header("Content-Type",  contentType)
                        .header("Accept",        accept);
                return this;
            }
            public Request contentType(String contentType) {
                return contentType(contentType, contentType);
            }

            public Body authorizationBearer(String token) {
                return authorization("Bearer", token);
            }
            public Body authorizationBasic(String credentials) {
                return authorization("Basic", credentials);
            }
            public Body authorization(String authScheme, String credentials) {
                header("Authorization", authScheme + " " + credentials);
                return new Body();
            }
        }

        public class Body {
            public Response body(HttpRequest.BodyPublisher bodyPublisher) {
                requestBuilder.POST(bodyPublisher);
                return new Response();
            }
            public Response body(InputStream body) {
                return body(HttpRequest.BodyPublishers.ofInputStream(() -> body));
            }
            public Response body(String body, Charset charset) {
                return body(HttpRequest.BodyPublishers.ofString(body, charset));
            }
            public Response body(String body) {
                return body(HttpRequest.BodyPublishers.ofString(body));
            }
        }

        public final class Response {

            @FunctionalInterface
            public interface ThrowingConsumer<I, O> {
                void apply(I i) throws IOException;
            }

            private HttpResponse response;

            public Response onOK(ThrowingConsumer<InputStream, O> handler) {
                handler.apply(response.body());
                return this;
            }
        }
    }
}