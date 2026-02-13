import lombok.SneakyThrows;

public class Playground {
    @SneakyThrows
    public static void main(String[] args) {
        var agent = new SampleAgent(
                "stepfun/step-3.5-flash:free",
                "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");
        System.out.println(
                agent.prompt("Привет! Тестирую возможности api. Ответь что-нибудь, чтобы я удостоверился, что всё работает.")
        );
    }
}