import com.cybershrek.jaio.agent.RestApiAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class SampleAgent extends RestApiAgent<String, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void configure(Configurator configurator) {
        configurator
                .requestLog(true)
                .responseLog(true)
        ;
    }

    @Override
    protected void onInput(String input, RequestConfigurator requestConfigurator) throws IOException {
        memory.addMessage("user", input);

//        requestConfigurator.toUrl("https://openrouter.ai/api/v1/chat/completions")
//                .withAuthorizationBearer("")
//                .sendJson(mapper.writeValueAsString(Map.of(
//                        "model", "stepfun/step-3.5-flash:free",
//                        "messages", memory.getMessages()
//                )))
//                .thenAcceptJson(body -> mapper.readTree(body)
//                        .path("choices")
//                        .path(0)
//                        .path("message")
//                        .path("content")
//                        .asText());

        requestConfigurator.toUrl("https://openrouter.ai/api/v1/chat/completions")
                .withAuthorizationBearer("")
                .sendJson(mapper.writeValueAsString(Map.of(
                        "model", "stepfun/step-3.5-flash:free",
                        "messages", memory.getMessages(),
                        "stream", true
                )))
                .thenAcceptSseStream(chunks -> {

                    var stringBuilder = new StringBuilder();
                    for (var chunk : chunks) {
                        var json = mapper.readTree(chunk);
                        System.out.println(json);
                        var reasoning = json
                                .path("choices")
                                .path(0)
                                .path("reasoning")
                                .asText();
                        if (reasoning != null)
                            stringBuilder.append(reasoning);
                        stringBuilder
                                .append(json
                                        .path("choices")
                                        .path(0)
                                        .path("content")
                                        .asText());
                    }
                    return stringBuilder.toString();
                });
    }
}
