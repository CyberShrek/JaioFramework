package com.cybershrek.jaio.agent.api.http;

import java.net.MalformedURLException;
import java.net.URL;

public record RestModel(
        String name,
        String url,
        String apiKey,
        String instruction
) {
    public RestModel {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}
