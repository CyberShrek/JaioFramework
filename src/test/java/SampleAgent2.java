import com.cybershrek.jaio.agent.http.HttpAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class SampleAgent2 extends HttpAgent<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;
    private final String apiKey;

    @Override
    protected void configure(Configurator configurator) throws IOException {
        configurator
                .onInput(content -> context.addMessage("user", content))
                .url("https://openrouter.ai/api/v1/chat/completions")
                .authorizationBearer(apiKey)
                .body(mapper.writeValueAsString(Map.of(
                        "model", model,
                        "messages", context.getMessages()
                )))
                .onOK(body -> mapper.readTree(body)
                        .path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText())
                .onError(e -> {})
                .onOutput(content -> context.addMessage("assistant", content));
    }
}