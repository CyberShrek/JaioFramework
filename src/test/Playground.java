import java.io.IOException;

public class Playground {

    public static void main(String[] args) throws IOException {
        var agent = new SampleAgent();

        System.out.println(agent.prompt("Привет! Тестирую api. Напиши кратко, если всё ок", System.out::println));
    }
}