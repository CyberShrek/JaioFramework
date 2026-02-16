import com.cybershrek.jaio.agent.http.HttpAgent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;

import com.cybershrek.jaio.agent.AgentContext;
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
        useContext(new AgentContext());
    }

    @Override
    protected HttpRequest buildRequest(String input) throws IOException {
        context.addMessage("user", input);
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
        var output = mapper.readTree(body)
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();

        context.addMessage("assistant", output);
        return output;
    }
}