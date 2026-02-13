package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.Agent;
import com.cybershrek.jaio.exception.HttpAgentException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

/**
 * Base class for HTTP-based agents. Handles sending HTTP requests and processing responses.
 * Manages an {@link HttpClient} instance and provides lifecycle hooks for request building
 * and response handling. The response {@link InputStream} is always closed after processing.
 *
 * @param <I> the type of the input
 * @param <O> the type of the output
 */
public abstract class HttpAgent<I, O> implements Agent<I, O> {

    protected final HttpClient client;
    private static final HttpClient DEFAULT_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Constructs an HttpAgent with a custom HTTP client.
     *
     * @param client the HTTP client to use; must not be null
     */
    protected HttpAgent(HttpClient client) {
        Objects.requireNonNull(client, "Client cannot be null");
        this.client = client;
    }

    /**
     * Constructs an HttpAgent with the default HTTP client.
     */
    protected HttpAgent() {
        this(DEFAULT_CLIENT);
    }

    /**
     * Sends the request derived from the input and returns the processed response.
     * Interruption of the sending thread is properly propagated.
     *
     * @param input the input from which the request is built
     * @return the processed response
     * @throws HttpAgentException if the request fails due to I/O, interruption, or an HTTP error
     */
    @Override
    public O prompt(I input) throws HttpAgentException {
        try {
            HttpRequest request = buildRequest(input);
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return handleResponse(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpAgentException("Request interrupted", e);
        } catch (IOException e) {
            throw new HttpAgentException("I/O error during request", e);
        }
    }

    /**
     * Builds an HTTP request from the given input.
     *
     * @param input the input data
     * @return the constructed {@link HttpRequest}
     * @throws IOException if an I/O error occurs during request construction
     * @throws HttpAgentException for agent-specific request building failures
     */
    protected abstract HttpRequest buildRequest(I input) throws IOException, HttpAgentException;

    /**
     * Processes a successful (2xx) HTTP response.
     *
     * @param response the HTTP response; the body stream will be closed afterwards
     * @return the processed result
     * @throws IOException if an I/O error occurs while reading the response
     * @throws HttpAgentException for agent-specific handling failures
     */
    protected abstract O onSuccess(HttpResponse<InputStream> response) throws IOException, HttpAgentException;

    /**
     * Processes an informational (1xx) HTTP response.
     * Default implementation throws an exception with the status code.
     *
     * @param response the HTTP response; the body stream will be closed afterwards
     * @return never returns normally
     * @throws IOException if an I/O error occurs
     * @throws HttpAgentException always thrown with the status code
     */
    protected O onInformational(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Informational", response);
    }

    /**
     * Processes a redirection (3xx) HTTP response.
     * Default implementation throws an exception with the status code.
     *
     * @param response the HTTP response; the body stream will be closed afterwards
     * @return never returns normally
     * @throws IOException if an I/O error occurs
     * @throws HttpAgentException always thrown with the status code
     */
    protected O onRedirection(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Redirection", response);
    }

    /**
     * Processes an error (4xx or 5xx) HTTP response.
     * Default implementation throws an exception with the status code.
     *
     * @param response the HTTP response; the body stream will be closed afterwards
     * @return never returns normally
     * @throws IOException if an I/O error occurs
     * @throws HttpAgentException always thrown with the status code
     */
    protected O onError(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        throw new HttpAgentException("Error", response);
    }

    /**
     * Dispatches the response to the appropriate handler based on its status code.
     * Ensures the response body {@link InputStream} is closed after the handler completes.
     *
     * @param response the HTTP response
     * @return the result from the status‑specific handler
     * @throws IOException if an I/O error occurs
     * @throws HttpAgentException if the handler throws an agent exception
     */
    protected O handleResponse(HttpResponse<InputStream> response) throws IOException, HttpAgentException {
        int code = response.statusCode();
        try {
            if (code >= 100 && code < 200)
                return onInformational(response);

            if (code >= 200 && code < 300)
                return onSuccess(response);

            if (code >= 300 && code < 400)
                return onRedirection(response);

            return onError(response);
        } finally {
            response.body().close();
        }
    }
}