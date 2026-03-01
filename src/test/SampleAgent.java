import com.cybershrek.jaio.agent.RestApiAgent;

import java.io.IOException;

public class SampleAgent extends RestApiAgent<String, String> {

    @Override
    protected void onInput(String input, Steps strategy) throws IOException {
        strategy.toUrl("url")
                .withAuthorizationBearer("")
                .sendJson("")
                .thenAcceptJson(json -> {
                    return "json";
                });
    }
}
