package com.cybershrek.jaio.agent;

import java.util.Map;

public abstract class Agent<I, O> {

    protected abstract Map<String, String> headers(String token);

    protected abstract Map<String, Object> body(I input);

    public final O prompt(I input) {
        return request(input);
    };
}
