import com.cybershrek.HandyAgent;

public class Playground {
    public static void main(String[] args) {
        HandyAgent llm = new HandyAgent("tngtech/deepseek-r1t2-chimera:free");
        System.out.println(
                llm.chat("Привет! Тестирую возможности api. Ответь что-нибудь, чтобы я удостоверился, что всё работает.")
        );
    }
}