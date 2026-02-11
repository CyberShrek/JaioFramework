import com.cybershrek.jaio.agent.http.HttpAgent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SampleAgent extends HttpAgent<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected HttpRequest buildRequest(String message) throws JsonProcessingException {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(Map.of(
                                "model", "tngtech/deepseek-r1t2-chimera:free",
                                "messages", List.of(message)
                        ))
                )).build();
    }

    @Override
    protected String handleResponse(HttpResponse<InputStream> response) throws IOException {
        return mapper.readTree(response.body())
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();
    }
}