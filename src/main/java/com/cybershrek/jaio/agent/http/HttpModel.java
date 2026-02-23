package com.cybershrek.jaio.agent.http;


import java.net.MalformedURLException;
import java.net.URL;

public record HttpModel (
        String name,
        String url,
        String apiKey
) {
    public HttpModel {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}
