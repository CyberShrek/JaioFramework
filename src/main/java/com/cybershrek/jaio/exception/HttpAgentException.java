package com.cybershrek.jaio.exception;

import lombok.experimental.StandardException;

import java.io.IOException;
import java.io.InputStream;

@StandardException
public class HttpAgentException extends AgentException {

    public HttpAgentException(String title, int code) {
        super(title + ": " + code);
    }
}