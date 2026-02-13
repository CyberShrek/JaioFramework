import com.cybershrek.jaio.agent.http.HttpAgent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SampleAgent extends HttpAgent<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;
    private final String key;

    public SampleAgent(String model,
                       String key) {
        super();
        this.model = model;
        this.key = key;
    }

    @Override
    protected HttpRequest buildRequest(String message) throws IOException {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(Map.of(
                                "model", model,
                                "messages", List.of(Map.of("role", "user", "content", message))
                        ))
                )).build();
    }

    @Override
    protected String onSuccess(HttpResponse<InputStream> response) throws IOException {
        return mapper.readTree(response.body())
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();
    }
}