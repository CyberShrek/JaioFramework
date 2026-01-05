package com.cybershrek;

import com.cybershrek.tools.HandyClient;
import com.cybershrek.tools.HandyResources;
import com.cybershrek.tools.JSON;

import java.io.IOException;
import java.util.*;

public class HandyAgent {

    private final String model;
    private final HandyClient client = new HandyClient();
    private final LinkedList<Map<String, String>> messages = new LinkedList<>();

    public HandyAgent(String model, String systemPrompt) {
        this.model = model;
        Properties props = HandyResources.loadProperties("private.properties");
        client.url(props.getProperty("openrouter.url"))
              .header("Authorization", "Bearer " + props.getProperty("openrouter.key"));

        addMessage("system", systemPrompt);
    }
    public HandyAgent(String model) {
        this(model, "");
    }

    public String ask(String prompt, double temperature) {

        addMessage("user", prompt);

        String response = JSON.parse(client
                .body(JSON.stringify(Map.of(
                        "model", model,
                        "temperature", temperature,
                        "messages", List.of(
                                Map.of(
                                        "role", "user",
                                        "content", prompt
                                )
                        )
                )))
                .POST()
                .body()).get("choices").get(0).get("message").get("content").asText();

        addMessage("assistant", response);

        return response;
    }
    public String ask(String prompt) {
        return ask(prompt, 0.7);
    }

    private void addMessage(String role, String content) {
        messages.add(Map.of(
                "role", role,
                "content", content
        ));
    }
}
