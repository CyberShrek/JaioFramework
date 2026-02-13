import lombok.SneakyThrows;

public class Playground {
    @SneakyThrows
    public static void main(String[] args) {
        var agent = new SampleAgent(
                "tngtech/deepseek-r1t2-chimera:free",
                "");
        System.out.println(
                agent.prompt("Привет! Тестирую возможности api. Ответь что-нибудь, чтобы я удостоверился, что всё работает.")
        );
    }
}