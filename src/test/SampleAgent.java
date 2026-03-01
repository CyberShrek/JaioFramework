import com.cybershrek.jaio.agent.RestApiAgent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class SampleAgent extends RestApiAgent<String, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void configure(RestApiAgent<String, String>.Config config) {
        config.requestAttempts(1, 0);
    }

    @Override
    protected void onInput(String input, Steps strategy) throws IOException {
        context.addMessage("user", input);

        var json = mapper.writeValueAsString(Map.of(
                "model", "stepfun/step-3.5-flash:free",
                "messages", context.getMessages(),
                "stream", true
        ));

//        strategy.toUrl("https://openrouter.ai/api/v1/chat/completions")
//                .withAuthorizationBearer("")
//                .sendJson(json)
//                .thenAcceptJson(body -> mapper.readTree(body)
//                        .path("choices")
//                        .path(0)
//                        .path("message")
//                        .path("content")
//                        .asText());

        strategy.toUrl("https://openrouter.ai/api/v1/chat/completions")
                .withAuthorizationBearer("")
                .sendJson(json)
                .thenAcceptSseChunks(body -> "" + body.size());
    }
}
