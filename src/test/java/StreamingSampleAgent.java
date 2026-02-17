import com.cybershrek.jaio.agent.http.StreamingHttpAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class StreamingSampleAgent extends StreamingHttpAgent<String, String, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;
    private final String key;

    @Override
    protected HttpRequest configureRequest() throws IOException {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(Map.of(
                                "model", model,
                                "messages", context.getMessages(),
                                "stream", true
                        ))
                )).build();
    }

    @Override
    protected String readOkChunk(String data) throws IOException {
        var chunk = mapper.readTree(data)
                .path("choices")
                .path(0)
                .path("delta")
                .path("content")
                .asText(null);

        return chunk.isEmpty() ? null : chunk;
    }

    @Override
    protected String mergeChunks(List<String> chunks) {
        String output = String.join("", chunks);
        context.addMessage("assistant", output);
        return String.join("", chunks);
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