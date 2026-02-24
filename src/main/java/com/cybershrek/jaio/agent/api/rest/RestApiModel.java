package com.cybershrek.jaio.agent.api.rest;

import java.net.MalformedURLException;
import java.net.URL;

public record RestApiModel(
        String name,
        String url,
        String apiKey
) {
    public RestApiModel {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}
