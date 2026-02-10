import com.cybershrek.jaio.agent.http.HttpAgent;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

public class SampleAgent extends HttpAgent<String, String> {

    public SampleAgent() {
        super(input -> {
                    try {
                        return HttpRequest.newBuilder()
                                .uri(new URI("https://openrouter.ai/api/v1/chat/completions"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer ")
                                .POST(HttpRequest.BodyPublishers.ofString(input));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                },
                inputStream -> {
                    try {
                        return new String(inputStream.readAllBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
