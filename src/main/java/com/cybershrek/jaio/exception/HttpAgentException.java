package com.cybershrek.jaio.exception;

import lombok.experimental.StandardException;

import java.net.http.HttpResponse;

@StandardException
public class HttpAgentException extends AgentException {

    public HttpAgentException(String title, HttpResponse<String> response) {
        super(title + ": " + response.statusCode() + "\n" + response.body());
    }
}