package com.cybershrek.jaio.exception;

import lombok.SneakyThrows;
import lombok.experimental.StandardException;

import java.io.InputStream;
import java.net.http.HttpResponse;

@StandardException
public class HttpAgentException extends AgentException {

    public HttpAgentException(String title, int code, String message) {
        super(title + ": " + code + "\n" + message);
    }

    public HttpAgentException(String title, HttpResponse<InputStream> response) {
        this(title, response.statusCode(), readBody(response));
    }

    @SneakyThrows
    private static String readBody(HttpResponse<InputStream> response) {
        try (var body = response.body()) {
            return new String(body.readAllBytes());
        }
    }
}