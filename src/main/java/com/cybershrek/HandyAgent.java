package com.cybershrek;

import com.cybershrek.jaio.agent.AgentConfig;
import com.cybershrek.tools.HandyClient;
import com.cybershrek.tools.HandyResources;
import com.cybershrek.tools.JSON;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandyAgent {

    private final AgentConfig config;

    private final HandyClient client = new HandyClient();
    private final List<Map<String, String>> instructions = new ArrayList<>();
    private final LinkedList<Map<String, String>> messages = new LinkedList<>();
    private final Properties props;

    public HandyAgent(String model) {this(AgentConfig.builder().model(model).build());}
    public HandyAgent(AgentConfig config) {
        this.config = config;
        props = HandyResources.loadProperties("private.properties");
        client.url(props.getProperty("openrouter.url"))
              .header("Authorization", "Bearer " + props.getProperty("openrouter.key"));

        addInstruction("system", config.getInstruction());
    }

    public String chat(String prompt) {

        if (getMessagesSizeInChars() > config.getMaxChars()){
            if (config.getAggregationAllowed())
                aggregateOldMessages();
            else
                cleanOldMessages();
        }

        addMessage("user", prompt);

        var body = JSON.stringify(Map.of(
                "model", config.getModel(),
                "temperature", config.getTemperature(),
                "messages", Stream.concat(instructions.stream(), messages.stream()).collect(Collectors.toList()),
                "instructions", instructions.getFirst().get("content")
        ));
        var response = client
                        .body(body)
                        .POST();

        if (response.statusCode() != 200)
            throw new RuntimeException("Something went wrong:\nRequest body: " + body + "\nResponse body: " + response.body());

        String message = JSON.parse(response
                .body()).get("choices").get(0).get("message").get("content").asText();

        addMessage("assistant", message);

        return message;
    }

    public String instruct(String prompt) {
        addInstruction("user", prompt);
        return chat(prompt);
    }

    private void addInstruction(String role, String content) {
        instructions.add(Map.of(
                "role", role,
                "content", content
        ));
    }

    private void addMessage(String role, String content) {
        messages.add(Map.of(
                "role", role,
                "content", content
        ));
    }

    private void cleanOldMessages() {

        if (config.getAggregationAllowed() && getMessagesSizeInChars() > config.getMaxChars()) {
            System.out.print("АГГРЕГАЦИЯ:");
            aggregateOldMessages();
            System.out.println(messages.getFirst().get("content"));
            System.out.println("Текущий размер: " + getMessagesSizeInChars());
        }

        while (config.getMaxChars() <= getMessagesSizeInChars()) {
            messages.removeFirst();
        }
    }

    private void aggregateOldMessages() {
        HandyAgent agent = new HandyAgent(AgentConfig.builder()
                .model(config.getModel())
                .instruction(HandyResources.loadText("aggregator.prompt"))
                .temperature(0.5)
                .aggregationAllowed(false)
                .build());

        System.out.print("АГГРЕГАЦИЯ. Старый размер: " + getMessagesSizeInChars());

        var aggregableMessages = new ArrayList<Map<String, String>>();
        while (getMessagesSizeInChars() >= config.getMinChars()) {
            aggregableMessages.add(messages.removeFirst());
        }

        String aggregate = agent.chat(JSON.stringify(aggregableMessages));
        System.out.println(aggregate);
        messages.addFirst(Map.of(
                "role", "assistant",
                "content", aggregate
        ));
        System.out.println("Новый размер: " + getMessagesSizeInChars());
    }

    private Integer getMessagesSizeInChars() {
        return messages.stream().flatMap(m -> m.values().stream()).mapToInt(String::length).sum();
    }
}
