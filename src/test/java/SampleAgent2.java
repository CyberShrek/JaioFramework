import com.cybershrek.jaio.agent.http.HttpAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class SampleAgent2 extends HttpAgent<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private final String model;

    @Override
    protected void onInput(String input, CallChain chain) throws IOException {
        chain
                .systemMessage("")
                .userMessage(input)
                .url("https://openrouter.ai/api/v1/chat/completions")
                .authorizationBearer(apiKey)
                .send(mapper.writeValueAsString(Map.of(
                        "model", model,
                        "messages", context.getMessages()
                )))
                .handleResponse(body -> mapper.readTree(body)
                        .path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText());
    }
}