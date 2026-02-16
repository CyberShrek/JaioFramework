import com.cybershrek.jaio.agent.http.HttpAgent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleAgent extends HttpAgent<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;
    private final String key;


    @Override
    protected HttpRequest buildRequest() throws IOException {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type",  "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(Map.of(
                                "model", model,
                                "messages", context.getMessages()
                        ))
                )).build();
    }

    @Override
    protected String readOkBody(InputStream body) throws IOException {
        return mapper.readTree(body)
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();
    }

    @Override
    protected void addUserMessage(String input) {
        context.addMessage("user", input);
    }

    @Override
    protected void addAgentMessage(String output) {
        context.addMessage("assistant", output);
    }
}