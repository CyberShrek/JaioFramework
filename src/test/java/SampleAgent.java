import com.cybershrek.jaio.agent.http.HttpAgent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleAgent extends HttpAgent<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;
    private final String apiKey;

    @Override
    protected void onInput(CallChain chain) throws IOException {
        chain
                .url("https://openrouter.ai/api/v1/chat/completions")
                .authorizationBearer(apiKey)
                .body(mapper.writeValueAsString(Map.of(
                        "model", model,
                        "messages", context.getMessages()
                )));
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
    protected void onInput(String content) {
        context.addMessage("user", content);
    }

    @Override
    protected void onOutput(String content) {
        context.addMessage("assistant", content);
    }
}