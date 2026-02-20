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

public abstract class HttpAgent<I, O> extends Agent<I, O> {

    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    protected final HttpClient client = DEFAULT_CLIENT;

    private final CallChain chain = new CallChain();

    @Override
    public synchronized O prompt(I input) throws HttpAgentException {
        try {
            return onResponse(client
                    .sendAsync(buildRequest(),
                            HttpResponse.BodyHandlers.ofInputStream())
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

    protected abstract void onInput(I input, CallChain chain) throws IOException;

    protected HttpRequest buildRequest() {
        return HttpRequest.newBuilder()
                .build();
    }

    protected O readOkBody(InputStream body) throws IOException {
        return null;
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

    protected class CallChain {

        private ThrowingFunction<I, Object> inputCallback;

        public CallChain systemMessage(Object content) {
            context.addSystemMessage(content);
            return this;
        }

        public InputLink userMessage(Object content){
            return new InputLink();
        }

        public class InputLink {

            public Request url(String url) {
                requestBuilder.uri(URI.create(url));
                return new Request();
            }
        }

        protected class OutputLink {

        }


        protected class UserLink {

        }



        @FunctionalInterface
        public interface ThrowingConsumer<I> {
            void apply(I i) throws IOException;
        }
        @FunctionalInterface
        public interface ThrowingFunction<I, O> {
            O apply(I i) throws IOException;
        }

        private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        protected HttpRequest buildRequest() {
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
            public Response send(HttpRequest.BodyPublisher bodyPublisher) {
                requestBuilder.POST(bodyPublisher);
                return new Response();
            }
            public Response send(InputStream body) {
                return send(HttpRequest.BodyPublishers.ofInputStream(() -> body));
            }
            public Response send(String body, Charset charset) {
                return send(HttpRequest.BodyPublishers.ofString(body, charset));
            }
            public Response send(String body) {
                return send(HttpRequest.BodyPublishers.ofString(body));
            }
        }

        public final class Response {

            @FunctionalInterface
            public interface ThrowingConsumer<I, O> {
                void apply(I i) throws IOException;
            }

            private HttpResponse response;

            public Response handleResponse(ThrowingConsumer<InputStream, O> handler) {
//                handler.apply(response.body());
                return this;
            }
        }
    }
}