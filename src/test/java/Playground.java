import com.cybershrek.jaio.exception.HttpAgentException;
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

public class Playground {
    @SneakyThrows
    public static void main(String[] args) {
        var agent = new SampleAgent(
                "stepfun/step-3.5-flash:free",
                "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");
        var streamingAgent = new StreamingSampleAgent(
                "stepfun/step-3.5-flash:free",
                "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");

        var future1 = CompletableFuture
                .runAsync(() -> {
                    try {
                        System.out.println("1: " + agent.prompt("Привет! Тестирую возможности api. Напиши что-нибудь короткое, чтобы я удостоверился, что всё работает."));
                    } catch (HttpAgentException e) {
                        throw new RuntimeException(e);
                    }
                });
        var future2 = CompletableFuture
                .runAsync(() -> {
                    try {
                        System.out.println("2: " + agent.prompt("Привет! Тестирую возможности api. Напиши что-нибудь короткое, чтобы я удостоверился, что всё работает."));
                    } catch (HttpAgentException e) {
                        throw new RuntimeException(e);
                    }
                });
        var future3 = CompletableFuture
                .runAsync(() -> {
                    try {
                        System.out.println("3: " + agent.prompt("Привет! Тестирую возможности api. Напиши что-нибудь короткое, чтобы я удостоверился, что всё работает."));
                    } catch (HttpAgentException e) {
                        throw new RuntimeException(e);
                    }
                });
        var future4 = CompletableFuture
                .runAsync(() -> {
                    try {
                        System.out.println("4: " + agent.prompt("Привет! Тестирую возможности api. Напиши что-нибудь короткое, чтобы я удостоверился, что всё работает."));
                    } catch (HttpAgentException e) {
                        throw new RuntimeException(e);
                    }
                });
        var future5 = CompletableFuture
                .runAsync(() -> {
                    try {
                        System.out.println("5: " + agent.prompt("Привет! Тестирую возможности api. Напиши что-нибудь короткое, чтобы я удостоверился, что всё работает."));
                    } catch (HttpAgentException e) {
                        throw new RuntimeException(e);
                    }
                });

        future1.join();
        future2.join();
        future3.join();
        future4.join();
        future5.join();
    }
}